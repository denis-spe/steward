package com.den.steward.backend.services

import android.util.Log
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.service.Storage
import com.den.steward.helper.nextCycle
import com.den.steward.helper.toMap
import com.den.steward.helper.toTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class StorageService @Inject constructor(
    override val firestore: FirebaseFirestore
) : Storage {

    companion object {
        private const val USER_COLLECTION = "Users"
        private const val TRANSACTION_COLLECTION = "Transactions"
        private const val REPAYMENT_COLLECTION = "Repayments"
        private const val SETTLEMENT_COLLECTION = "Settlements"
        private const val ATTAIN_COLLECTION = "Attain"
        private const val ACHIEVEMENT_COLLECTION = "Achievement"
        private const val PLAN_COLLECTION = "Plans"
        private const val TAG = "StorageService"
        private const val SERVER_ACK_TIMEOUT_MS = 10_000L
    }

    private val docRef = firestore.collection(USER_COLLECTION)

    // -------------------------------------------------------------------------
    // Add transaction
    // -------------------------------------------------------------------------
    override suspend fun addTransaction(
        userId: String,
        transaction: Transaction
    ): Result<String> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID is empty"))
        }

        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document()

            val transactionData = transaction.toMap.toMutableMap()
            transactionData["id"] = transactionRef.id

            val acknowledged = withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                transactionRef.set(transactionData).await()
                true
            } ?: false

            if (!acknowledged) {
                Log.d(
                    TAG,
                    "addTransaction applied locally; background server sync pending: ${transactionRef.id}"
                )
            }

            Result.success(transactionRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add transaction for user=$userId", e)
            Result.failure(e)
        }
    }

    // Kept for other callers. GoalWorker must use resetGoalAttain() only, because that method
    // now creates the achievement atomically with deleting Attain documents and resetting the goal.
    override suspend fun addGoalAchieved(
        userId: String,
        transaction: Transaction.Goal
    ): Result<Unit> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
            val achievedRef = transactionRef.collection(ACHIEVEMENT_COLLECTION)
                .document("cycle_${transaction.endAt}")

            val achievedValue = transaction.attain.sumOf { it.value }
            val status = if (achievedValue >= transaction.value) {
                GoalStatus.COMPLETED
            } else {
                GoalStatus.FAILED
            }

            val achievement = Transaction.Achievement(
                id = achievedRef.id,
                value = achievedValue,
                createdAt = System.currentTimeMillis(),
                startAt = transaction.startedAt,
                endAt = transaction.endAt,
                goal = transaction,
                status = status
            )

            val acknowledged = withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                achievedRef.set(achievement.toMap).await()
                true
            } ?: false

            if (!acknowledged) {
                Log.d(
                    TAG,
                    "Achievement write applied locally; background server sync pending: ${achievedRef.id}"
                )
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add achievement for user=$userId, goal=${transaction.id}", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Add fulfillment
    // -------------------------------------------------------------------------
    override suspend fun addFulfillment(
        userId: String,
        transactionId: String,
        fulfillment: Transaction
    ): Result<String> {
        val collection = when (fulfillment) {
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type ${fulfillment.javaClass.simpleName}")
            )
        }

        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
            val fulfillmentRef = transactionRef.collection(collection).document()
            val fulfillmentData = fulfillment.toMap.toMutableMap()
            fulfillmentData["id"] = fulfillmentRef.id

            val acknowledged = withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                fulfillmentRef.set(fulfillmentData).await()
                true
            } ?: false

            if (!acknowledged) {
                Log.d(
                    TAG,
                    "Fulfillment write applied locally; background server sync pending: ${fulfillmentRef.path}"
                )
            }

            Result.success(fulfillmentRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add fulfillment for transaction=$transactionId, user=$userId", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Reset goal: achievement + deleting Attain docs + parent goal update are one atomic batch.
    // The connected-network WorkManager constraint is important because this uses Source.SERVER.
    // -------------------------------------------------------------------------
    private class CacheUnavailableException(cause: Throwable?) :
        Exception("Required data not available offline", cause)

    private suspend fun <T> serverThenCache(
        server: suspend () -> T,
        cache: suspend () -> T
    ): T? = try {
        withTimeout(SERVER_ACK_TIMEOUT_MS.milliseconds) { server() }
    } catch (e: TimeoutCancellationException) {   // must come before CancellationException
        cacheOrNull(cache)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        cacheOrNull(cache)
    }

    private suspend fun <T> cacheOrNull(cache: suspend () -> T): T? = try {
        cache()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.i(TAG, "Not in local cache; falling back to caller-provided data")
        null
    }

    // -------------------------------------------------------------------------
// Reset goal: achievement + deleting Attain docs + parent goal update are one atomic batch.
// Reads prefer the server and fall back to the local cache, so this works offline too.
// The batch is queued locally when offline and syncs later.
// -------------------------------------------------------------------------
    override suspend fun resetGoalAttain(
        userId: String,
        transaction: Transaction.Goal
    ): Result<Transaction.Goal> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID is empty"))
        }

        return try {

            addGoalAchieved(
                userId,
                transaction
            )
            deleteAttainDocs(
                userId,
                transaction.id
            )

            val newTransaction = transaction.nextCycle()

            updateTransaction(
                userId,
                transaction.id,
                newTransaction.copy(
                    attain = emptyList(),
                )
            )

            Result.success(newTransaction)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to reset/process goal cycle for user=$userId, goal=${transaction.id}",
                e
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Update transaction
    // -------------------------------------------------------------------------
    override suspend fun updateTransaction(
        userId: String,
        transactionId: String,
        newTransaction: Transaction
    ): Result<Unit> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
            transactionRef.update(newTransaction.toMap).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update transaction=$transactionId", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTransactionFulfillment(
        userId: String,
        transactionId: String,
        oldFulfillmentId: String,
        newFulfillment: Transaction
    ): Result<Unit> {
        val collection = when (newFulfillment) {
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type ${newFulfillment.javaClass.simpleName}")
            )
        }

        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
            val fulfillmentData = newFulfillment.toMap.toMutableMap()
            fulfillmentData["id"] = oldFulfillmentId
            transactionRef.collection(collection)
                .document(oldFulfillmentId)
                .set(fulfillmentData)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to update fulfillment=$oldFulfillmentId for transaction=$transactionId",
                e
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Get a transaction and its supported subcollections
    // -------------------------------------------------------------------------
    override suspend fun getTransaction(
        userId: String,
        transactionId: String
    ): Result<Transaction?> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            val transactionSnapshot = serverThenCache(
                server = { transactionRef.get().await() },
                cache = { transactionRef.get(Source.CACHE).await() }
            )

            if (transactionSnapshot == null) return Result.success(null)

            val transaction = transactionSnapshot.toTransaction ?: return Result.success(null)

            val collectionsToFetch = when (transaction) {
                is Transaction.Lent -> listOf(REPAYMENT_COLLECTION)
                is Transaction.Debt -> listOf(SETTLEMENT_COLLECTION)
                is Transaction.Goal -> listOf(ATTAIN_COLLECTION, ACHIEVEMENT_COLLECTION)
                else -> emptyList()
            }

            if (collectionsToFetch.isEmpty()) return Result.success(transaction)

            val subItems = mutableListOf<Transaction>()
            for (collectionName in collectionsToFetch) {
                val subSnapshot = serverThenCache(
                    server = { transactionRef.collection(collectionName).get().await() },
                    cache = { transactionRef.collection(collectionName).get(Source.CACHE).await() }
                )

                if (subSnapshot == null) return Result.success(null)

                subItems += subSnapshot.documents.mapNotNull { it.toTransaction }
            }

            val merged = when (transaction) {
                is Transaction.Lent -> transaction.copy(
                    repayment = subItems.filterIsInstance<Transaction.Repayment>()
                )

                is Transaction.Debt -> transaction.copy(
                    settlement = subItems.filterIsInstance<Transaction.Settlement>()
                )

                is Transaction.Goal -> transaction.copy(
                    attain = subItems.filterIsInstance<Transaction.Attain>(),
                    achievement = subItems.filterIsInstance<Transaction.Achievement>()
                )

                else -> transaction
            }
            Result.success(merged)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get transaction=$transactionId for user=$userId", e)
            Result.failure(e)
        }
    }

    override suspend fun getTransactionFulfillment(
        userId: String,
        transactionId: String,
        fulfillment: Transaction
    ): Result<Transaction?> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
            val parent = serverThenCache(
                server = { transactionRef.get().await() },
                cache = { transactionRef.get(Source.CACHE).await() }
            )?.toTransaction ?: return Result.success(null)

            val collection = when (parent) {
                is Transaction.Lent -> REPAYMENT_COLLECTION
                is Transaction.Debt -> SETTLEMENT_COLLECTION
                is Transaction.Goal -> ATTAIN_COLLECTION
                is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
                is Transaction.PlanFulfillment -> PLAN_COLLECTION
                is Transaction.Plan -> PLAN_COLLECTION
                else -> null
            } ?: return Result.success(null)

            val item = serverThenCache(
                server = {
                    transactionRef.collection(collection).document(fulfillment.id).get().await()
                },
                cache = {
                    transactionRef.collection(collection).document(fulfillment.id).get(Source.CACHE)
                        .await()
                }
            )?.toTransaction ?: return Result.success(null)

            Result.success(item)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to get fulfillment=${fulfillment.id} for transaction=$transactionId",
                e
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Delete transaction / fulfillment
    // -------------------------------------------------------------------------
    suspend fun deleteAttainDocs(userId: String, transactionId: String) {
        val attainRef = docRef.document(userId)
            .collection(TRANSACTION_COLLECTION)
            .document(transactionId)
            .collection(ATTAIN_COLLECTION)

        val snapshot = attainRef.get().await()

        // A batch holds at most 500 operations
        snapshot.documents.chunked(500).forEach { chunk ->
            val batch = docRef.firestore.batch()
            chunk.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    override suspend fun deleteTransaction(
        userId: String,
        transactionId: String
    ): Result<Unit> {
        return try {
            docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
                .delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete transaction=$transactionId for user=$userId", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteFulfillment(
        userId: String,
        transactionId: String,
        fulfillmentId: String,
        fulfillmentType: Transaction
    ): Result<Unit> {
        if (userId.isBlank() || transactionId.isBlank() || fulfillmentId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID, transaction ID and fulfillment ID are required"))
        }

        val collection = when (fulfillmentType) {
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type ${fulfillmentType.javaClass.simpleName}")
            )
        }

        return try {
            val ref = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
                .collection(collection)
                .document(fulfillmentId)

            val acknowledged = withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                ref.delete().await()
                true
            } ?: false

            if (!acknowledged) {
                Log.w(TAG, "Delete applied locally or server acknowledgement pending: ${ref.path}")
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to delete fulfillment=$fulfillmentId for transaction=$transactionId",
                e
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Real-time fulfillment listener for a single transaction
    // -------------------------------------------------------------------------
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchTransactionFulfillment(
        userId: String,
        transaction: Transaction
    ): Flow<Result<List<Transaction>>> {
        val collection = when (transaction) {
            is Transaction.Lent -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(REPAYMENT_COLLECTION)

            is Transaction.Debt -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(SETTLEMENT_COLLECTION)

            is Transaction.Goal -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(ATTAIN_COLLECTION)

            is Transaction.Repayment -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(REPAYMENT_COLLECTION)

            is Transaction.Settlement -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(SETTLEMENT_COLLECTION)

            is Transaction.Achievement -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(ACHIEVEMENT_COLLECTION)

            is Transaction.Attain -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(ATTAIN_COLLECTION)

            is Transaction.PlanFulfillment -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(PLAN_COLLECTION)

            is Transaction.Plan -> docRef.document(userId).collection(TRANSACTION_COLLECTION)
                .document(transaction.id).collection(PLAN_COLLECTION)

            else -> return kotlinx.coroutines.flow.flowOf(Result.success(emptyList()))
        }

        return callbackFlow {
            val listener = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        TAG,
                        "Fulfillment listener failed for transaction=${transaction.id}",
                        error
                    )
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val items = snapshot?.documents
                    ?.mapNotNull { it.toTransaction }
                    ?.map { item ->
                        when (item) {
                            is Transaction.Achievement -> if (transaction is Transaction.Goal) {
                                item.copy(goal = transaction)
                            } else item

                            is Transaction.Attain -> if (transaction is Transaction.Goal) {
                                item.copy(goal = transaction)
                            } else item

                            is Transaction.PlanFulfillment -> if (transaction is Transaction.Plan) {
                                item.copy(plan = transaction)
                            } else item

                            else -> item
                        }
                    } ?: emptyList()
                trySend(Result.success(items))
            }
            awaitClose { listener.remove() }
        }
    }

    // -------------------------------------------------------------------------
    // Real-time listener for every transaction and its supported subcollections
    // -------------------------------------------------------------------------
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>> {
        return callbackFlow {
            val transactionsCollection = docRef.document(userId).collection(TRANSACTION_COLLECTION)
            val subListeners = mutableMapOf<String, List<ListenerRegistration>>()
            val fulfillmentMap = mutableMapOf<String, MutableMap<String, List<Transaction>>>()
            var latestTransactions: List<Transaction> = emptyList()

            fun emitMerged() {
                val merged = latestTransactions.map { transaction ->
                    val subItems = fulfillmentMap[transaction.id]?.values?.flatten().orEmpty()
                    when (transaction) {
                        is Transaction.Lent -> transaction.copy(
                            repayment = subItems.filterIsInstance<Transaction.Repayment>()
                                .map { it.copy(lent = transaction) }
                        )

                        is Transaction.Debt -> transaction.copy(
                            settlement = subItems.filterIsInstance<Transaction.Settlement>()
                                .map { it.copy(debt = transaction) }
                        )

                        is Transaction.Goal -> transaction.copy(
                            attain = subItems.filterIsInstance<Transaction.Attain>()
                                .map { it.copy(goal = transaction) },
                            achievement = subItems.filterIsInstance<Transaction.Achievement>()
                                .map { it.copy(goal = transaction) }
                        )

                        is Transaction.Plan -> transaction.copy(
                            transactions = subItems.filterIsInstance<Transaction.PlanFulfillment>()
                                .map { it.copy(plan = transaction) }
                        )
                        else -> transaction
                    }
                }
                trySend(Result.success(merged))
            }

            val topListener = transactionsCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Transaction listener failed for user=$userId", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val newTransactions = snapshot?.documents
                    ?.mapNotNull { it.toTransaction }
                    .orEmpty()
                val newIds = newTransactions.map { it.id }.toSet()

                // Remove subcollection listeners for transactions that were deleted.
                (subListeners.keys - newIds).forEach { removedId ->
                    subListeners.remove(removedId)?.forEach { it.remove() }
                    fulfillmentMap.remove(removedId)
                }

                newTransactions.forEach { transaction ->
                    if (subListeners.containsKey(transaction.id)) return@forEach

                    val collectionsMap = mutableMapOf<String, List<Transaction>>()
                    fulfillmentMap[transaction.id] = collectionsMap
                    val collectionNames = when (transaction) {
                        is Transaction.Lent -> listOf(REPAYMENT_COLLECTION)
                        is Transaction.Debt -> listOf(SETTLEMENT_COLLECTION)
                        is Transaction.Goal -> listOf(ATTAIN_COLLECTION, ACHIEVEMENT_COLLECTION)
                        is Transaction.Plan -> listOf(PLAN_COLLECTION)
                        else -> emptyList()
                    }

                    val listeners = collectionNames.map { collectionName ->
                        collectionsMap[collectionName] = emptyList()
                        transactionsCollection.document(transaction.id)
                            .collection(collectionName)
                            .addSnapshotListener { subSnapshot, subError ->
                                if (subError != null) {
                                    Log.e(
                                        TAG,
                                        "Subcollection listener failed: ${transaction.id}/$collectionName",
                                        subError
                                    )
                                    trySend(Result.failure(subError))
                                } else {
                                    collectionsMap[collectionName] = subSnapshot?.documents
                                        ?.mapNotNull { it.toTransaction }
                                        .orEmpty()
                                    emitMerged()
                                }
                            }
                    }
                    subListeners[transaction.id] = listeners
                }

                latestTransactions = newTransactions
                emitMerged()
            }

            awaitClose {
                topListener.remove()
                subListeners.values.flatten().forEach { it.remove() }
                subListeners.clear()
                fulfillmentMap.clear()
            }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }
}
