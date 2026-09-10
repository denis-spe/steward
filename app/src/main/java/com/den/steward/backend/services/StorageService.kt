package com.den.steward.backend.services

import android.util.Log
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.service.Storage
import com.den.steward.helper.toMap
import com.den.steward.helper.toTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class StorageService @Inject constructor(
    override val firestore: FirebaseFirestore,
) : Storage {

    companion object {
        private const val USER_COLLECTION = "Users"
        private const val TRANSACTION_COLLECTION = "Transactions"
        private const val REPAYMENT_COLLECTION = "Repayments"
        private const val REFUND_COLLECTION = "Refunds"
        private const val ATTAIN_COLLECTION = "Attain"
        private const val ACHIEVEMENT_COLLECTION = "Achievement"
        private const val TAG = "StorageService"
        private const val SERVER_ACK_TIMEOUT_MS = 5_000L
    }

    private val docRef = firestore
        .collection(USER_COLLECTION)

    // ================================== Adding the transaction ==============================
    override suspend fun addTransaction(userId: String, transaction: Transaction): Result<String> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document()

            val transactionData = transaction.toMap
            transactionData["id"] = transactionRef.id

            // await the set operation to ensure data is persistent (locally) before returning
            // We use a timeout to prevent hanging indefinitely when the backend is unreachable.
            withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                transactionRef.set(transactionData).await()
            }
            
            Result.success(transactionRef.id)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Failed to initiate transaction add for user $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun addGoalAchieved(userId: String, transaction: Transaction.Goal): Result<Unit> {
        return try {
            val achievedRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ACHIEVEMENT_COLLECTION)
                .document()

            val finalGoal = transaction.calculateStatus(System.currentTimeMillis())

            val achievement = Transaction.Achievement(
                id = achievedRef.id,
                value = transaction.attain.sumOf { it.value },
                createdAt = System.currentTimeMillis(),
                startAt = transaction.startedAt,
                endAt = transaction.endAt,
                goal = transaction,
                status = finalGoal.status
            )

            val achievedData = achievement.toMap
            withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                achievedRef.set(achievedData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Failed to add transaction for user $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun addFulfillment(
        userId: String,
        transactionId: String,
        fulfillment: Transaction
    ): Result<Unit> {
        val collection = when (fulfillment) {
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Refund -> REFUND_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type " +
                        "${fulfillment.javaClass.simpleName}"))
        }
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            val fulfillmentData = fulfillment.toMap
            withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                transactionRef.collection(collection)
                    .add(fulfillmentData)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add fulfillment for transaction $transactionId (user $userId)", e)
            Result.failure(e)
        }
    }

    // ============================= Updating the transaction ===================================
    override suspend fun resetGoalAttain(userId: String, transaction: Transaction.Goal): Result<Transaction.Goal> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)

            val attainRef = transactionRef.collection(ATTAIN_COLLECTION)
            // Cache-first read: works offline if previously synced, may be empty if never cached.
            val snapshots = try {
                attainRef.get().await()
            } catch (e: Exception) {
                Log.e(TAG, "Remote fetch failed, trying CACHE", e)
                attainRef.get(Source.CACHE).await()
            }

            val now = System.currentTimeMillis()
            val resetGoal = transaction.copy(attain = emptyList())
                .calculateSchedule(now)
                .calculateStatus(now)

            val updates: Map<String, Any> = mapOf(
                "startedAt" to com.google.firebase.Timestamp(java.util.Date(resetGoal.startedAt)),
                "endAt" to com.google.firebase.Timestamp(java.util.Date(resetGoal.endAt)),
                "status" to resetGoal.status.name
            )

            val batch = firestore.batch()
            for (doc in snapshots.documents) {
                batch.delete(doc.reference)
            }
            batch.update(transactionRef, updates)

            // Commit is applied to local cache immediately regardless of connectivity.
            // The returned Task only completes once the server acknowledges it, so we
            // bound the wait instead of hanging indefinitely while offline.
            val committedServerSide = try {
                withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                    batch.commit().await()
                    true
                } ?: false
            } catch (e: Exception) {
                // A genuine commit failure (not a timeout) — rethrow to outer catch.
                throw e
            }

            if (!committedServerSide) {
                Log.w(TAG, "Goal reset for user $userId applied locally; server ack pending (offline?)")
            }

            Result.success(resetGoal)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Failed to reset goal for user $userId", e)
            Result.failure(e)
        }
    }
    override suspend fun updateTransaction(
        userId: String,
        transactionId: String,
        newTransaction: Transaction
    ): Result<Unit> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            val transactionData = newTransaction.toMap
            transactionRef.update(transactionData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update transaction $transactionId for user $userId", e)
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
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Refund -> REFUND_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type " +
                        "${newFulfillment.javaClass.simpleName}"))
        }
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            val fulfillmentData = newFulfillment.toMap
            transactionRef.collection(collection)
                .document(oldFulfillmentId)
                .set(fulfillmentData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add fulfillment for transaction $transactionId (user $userId)", e)
            Result.failure(e)
        }
    }

    // ======================== Getting the transaction ========================
    override suspend fun getTransaction(userId: String, transactionId: String): Result<Transaction?> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            val transaction = try {
                withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                    transactionRef.get().await()
                } ?: run {
                    Log.w(TAG, "Remote fetch timed out for transaction $transactionId, trying CACHE")
                    transactionRef.get(Source.CACHE).await()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Remote fetch failed for transaction $transactionId, trying CACHE", e)
                transactionRef.get(Source.CACHE).await()
            }.toTransaction ?: return Result.success(null)

            val fulfillmentCollection = when (transaction) {
                is Transaction.Lent -> REPAYMENT_COLLECTION
                is Transaction.Debt -> REFUND_COLLECTION
                is Transaction.Goal -> ATTAIN_COLLECTION
                else -> null
            }

            if (fulfillmentCollection != null) {
                val subItems = try {
                    withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                        transactionRef
                            .collection(fulfillmentCollection)
                            .get().await()
                    } ?: run {
                        Log.w(TAG, "Remote fetch timed out for fulfillment of $transactionId, trying CACHE")
                        transactionRef
                            .collection(fulfillmentCollection)
                            .get(Source.CACHE).await()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Remote fetch failed for fulfillment of $transactionId, trying CACHE", e)
                    transactionRef
                        .collection(fulfillmentCollection)
                        .get(Source.CACHE).await()
                }.documents.mapNotNull { it.toTransaction }
                
                val updatedTransaction = when (transaction) {
                    is Transaction.Lent -> transaction.copy(repayment = subItems.filterIsInstance<Transaction.Repayment>())
                    is Transaction.Debt -> transaction.copy(refund = subItems.filterIsInstance<Transaction.Refund>())
                    is Transaction.Goal -> transaction.copy(attain = subItems.filterIsInstance<Transaction.Attain>()).calculateStatus(System.currentTimeMillis())
                    else -> transaction
                }
                Result.success(updatedTransaction)
            } else {
                Result.success(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get transaction $transactionId for user $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun getTransactionFulfillment(
        userId: String,
        transactionId: String,
        fulfillment: Transaction
    ): Result<Transaction?> {
        val transactionRef = docRef.document(userId)
            .collection(TRANSACTION_COLLECTION)
            .document(transactionId)

        val transaction = transactionRef.get().await().toTransaction ?: return Result.success(null)

        val fulfillmentCollection = when (transaction) {
            is Transaction.Lent -> REPAYMENT_COLLECTION
            is Transaction.Debt -> REFUND_COLLECTION
            is Transaction.Goal -> ATTAIN_COLLECTION
            else -> null
        }

        if (fulfillmentCollection == null) {
            return Result.success(null)
        }

        val fulfillment = transactionRef.collection(fulfillmentCollection)
            .document(fulfillment.id)
            .get().await().toTransaction ?: return Result.success(null)

        return Result.success(fulfillment)
    }


    // ====================== Deleting the transaction ==============================
    override suspend fun deleteTransaction(userId: String, transactionId: String): Result<Unit> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)

            transactionRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete transaction $transactionId for user $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteFulfillment(
        userId: String,
        transactionId: String,
        fulfillmentId: String,
        fulfillmentType: Class<out Transaction>
    ): Result<Unit> {
        val collection = when (fulfillmentType) {
            Transaction.Repayment::class.java -> REPAYMENT_COLLECTION
            Transaction.Refund::class.java -> REFUND_COLLECTION
            Transaction.Attain::class.java -> ATTAIN_COLLECTION
            Transaction.Achievement::class.java -> ACHIEVEMENT_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type " +
                        "${fulfillmentType.simpleName}"))
        }
        return try {
            val fulfillmentRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transactionId)
                .collection(collection)
                .document(fulfillmentId)

            fulfillmentRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete fulfillment $fulfillmentId for transaction $transactionId and user $userId", e)
            Result.failure(e)
        }
    }


    // ======================= Fetching all transaction ==================================
    override fun fetchTransactionFulfillment(userId: String, transaction: Transaction): Flow<Result<List<Transaction>>> {
        val collection = when (transaction) {
            is Transaction.Lent -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(REPAYMENT_COLLECTION)

            is Transaction.Debt -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(REFUND_COLLECTION)

            is Transaction.Goal -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ATTAIN_COLLECTION)

            is Transaction.Achievement -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ACHIEVEMENT_COLLECTION)


            else -> return flowOf(Result.success(emptyList()))
        }

        return callbackFlow {
            val subListener = collection
                .addSnapshotListener { subSnapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Sub-collection listener failed for transaction ${transaction.id}", error)
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val subItems = subSnapshot?.documents?.mapNotNull { it.toTransaction } ?: emptyList()
                    trySend(Result.success(subItems))
                }
            awaitClose { subListener.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>> {
        return callbackFlow {
            val transactionsCollection = docRef.document(userId).collection(TRANSACTION_COLLECTION)
            val subListeners = mutableMapOf<String, ListenerRegistration>()
            val fulfillmentMap = mutableMapOf<String, List<Transaction>>()
            var latestTransactions = emptyList<Transaction>()

            fun emitMerged() {
                val merged = latestTransactions.map { transaction ->
                    val subItems = fulfillmentMap[transaction.id] ?: emptyList()
                    when (transaction) {
                        is Transaction.Lent -> transaction.copy(repayment = subItems.filterIsInstance<Transaction.Repayment>())
                        is Transaction.Debt -> transaction.copy(refund = subItems.filterIsInstance<Transaction.Refund>())
                        is Transaction.Goal -> transaction.copy(attain = subItems.filterIsInstance<Transaction.Attain>())
                            .calculateStatus(System.currentTimeMillis())
                        else -> transaction
                    }
                }
                trySend(Result.success(merged))
            }

            val topListener = transactionsCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val newTransactions = snapshot?.documents?.mapNotNull { it.toTransaction } ?: emptyList()
                val newIds = newTransactions.map { it.id }.toSet()

                // Clean up stale listeners
                val removedIds = subListeners.keys - newIds
                removedIds.forEach { id ->
                    subListeners.remove(id)?.remove()
                    fulfillmentMap.remove(id)
                }

                // Add new listeners for added transactions
                newTransactions.forEach { transaction ->
                    if (!subListeners.containsKey(transaction.id)) {
                        val subCollection = when (transaction) {
                            is Transaction.Lent -> transactionsCollection.document(transaction.id).collection(REPAYMENT_COLLECTION)
                            is Transaction.Debt -> transactionsCollection.document(transaction.id).collection(REFUND_COLLECTION)
                            is Transaction.Goal -> transactionsCollection.document(transaction.id).collection(ATTAIN_COLLECTION)
                            is Transaction.Achievement -> transactionsCollection.document(transaction.id).collection(ACHIEVEMENT_COLLECTION)
                            else -> null
                        }

                        if (subCollection != null) {
                            subListeners[transaction.id] = subCollection.addSnapshotListener { subSnapshot, subError ->
                                if (subError == null) {
                                    fulfillmentMap[transaction.id] = subSnapshot?.documents?.mapNotNull { it.toTransaction } ?: emptyList()
                                    emitMerged()
                                }
                            }
                        } else {
                            fulfillmentMap[transaction.id] = emptyList()
                        }
                    }
                }

                latestTransactions = newTransactions
                emitMerged()
            }

            awaitClose {
                topListener.remove()
                subListeners.values.forEach { it.remove() }
            }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }
}
