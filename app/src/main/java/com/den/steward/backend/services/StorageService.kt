package com.den.steward.backend.services

import android.util.Log
import com.den.steward.backend.entitles.GoalStatus
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
        private const val SETTLEMENT_COLLECTION = "Settlements"
        private const val ATTAIN_COLLECTION = "Attain"
        private const val ACHIEVEMENT_COLLECTION = "Achievement"
        private const val PLAN_COLLECTION = "Plans"
        private const val TAG = "StorageService"
        private const val SERVER_ACK_TIMEOUT_MS = 5_000L
    }

    private val docRef = firestore
        .collection(USER_COLLECTION)

    // ================================== Adding the transaction ==============================
    override suspend fun addTransaction(userId: String, transaction: Transaction): Result<String> {
        Log.d(TAG, "addTransaction: type=${transaction.type.name}, userId=$userId")
        return try {
            if (userId.isEmpty()) {
                Log.e(TAG, "addTransaction failed: userId is empty")
                return Result.failure(IllegalArgumentException("User ID is empty"))
            }

            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document()

            val transactionData = transaction.toMap
            transactionData["id"] = transactionRef.id
            
            Log.d(TAG, "Writing transaction document to ${transactionRef.path}: $transactionData")

            // await the set operation to ensure data is persistent (locally) before returning
            // We use a timeout to prevent hanging indefinitely when the backend is unreachable.
            withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                transactionRef.set(transactionData).await()
            }
            
            Log.i(TAG, "Transaction document set successfully: ${transactionRef.id}")
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

            val status = when {
                transaction.attain.sumOf { it.value } >= transaction.value -> GoalStatus.COMPLETED
                else -> GoalStatus.FAILED
            }

            val achievement = Transaction.Achievement(
                id = achievedRef.id,
                value = transaction.attain.sumOf { it.value },
                createdAt = System.currentTimeMillis(),
                startAt = transaction.startedAt,
                endAt = transaction.endAt,
                goal = transaction,
                status = status
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
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
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
            val achievementRef = transactionRef.collection(ACHIEVEMENT_COLLECTION)

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
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
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

            val collectionsToFetch = when (transaction) {
                is Transaction.Lent -> listOf(REPAYMENT_COLLECTION)
                is Transaction.Debt -> listOf(SETTLEMENT_COLLECTION)
                is Transaction.Goal -> listOf(ATTAIN_COLLECTION, ACHIEVEMENT_COLLECTION)
                else -> emptyList()
            }

            if (collectionsToFetch.isNotEmpty()) {
                val subItems = mutableListOf<Transaction>()
                for (collectionName in collectionsToFetch) {
                    val fetchedItems = try {
                        withTimeoutOrNull(SERVER_ACK_TIMEOUT_MS.milliseconds) {
                            transactionRef
                                .collection(collectionName)
                                .get().await()
                        } ?: run {
                            Log.w(TAG, "Remote fetch timed out for fulfillment $collectionName of $transactionId, trying CACHE")
                            transactionRef
                                .collection(collectionName)
                                .get(Source.CACHE).await()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Remote fetch failed for fulfillment $collectionName of $transactionId, trying CACHE", e)
                        transactionRef
                            .collection(collectionName)
                            .get(Source.CACHE).await()
                    }.documents.mapNotNull { it.toTransaction }
                    subItems.addAll(fetchedItems)
                }
                
                val updatedTransaction = when (transaction) {
                    is Transaction.Lent -> transaction.copy(repayment = subItems.filterIsInstance<Transaction.Repayment>())
                    is Transaction.Debt -> transaction.copy(settlement = subItems.filterIsInstance<Transaction.Settlement>())
                    is Transaction.Goal -> transaction.copy(
                        attain = subItems.filterIsInstance<Transaction.Attain>(),
                        achievement = subItems.filterIsInstance<Transaction.Achievement>()
                    )
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
            is Transaction.Debt -> SETTLEMENT_COLLECTION
            is Transaction.Goal -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            is Transaction.PlanFulfillment -> PLAN_COLLECTION
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
        fulfillmentType: Transaction
    ): Result<Unit> {
        val collection = when (fulfillmentType) {
            is Transaction.Settlement -> SETTLEMENT_COLLECTION
            is Transaction.Repayment -> REPAYMENT_COLLECTION
            is Transaction.Attain -> ATTAIN_COLLECTION
            is Transaction.Achievement -> ACHIEVEMENT_COLLECTION
            else -> return Result.failure(
                IllegalArgumentException("Invalid fulfillment type " +
                        fulfillmentType.type.name
                ))
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
                .collection(SETTLEMENT_COLLECTION)

            is Transaction.Goal -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ATTAIN_COLLECTION)

            is Transaction.Repayment -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(REPAYMENT_COLLECTION)

            is Transaction.Settlement -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(SETTLEMENT_COLLECTION)


            is Transaction.Achievement -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ACHIEVEMENT_COLLECTION)

            is Transaction.Attain -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(ATTAIN_COLLECTION)

            is Transaction.PlanFulfillment -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(PLAN_COLLECTION)

            is Transaction.Plan -> docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document(transaction.id)
                .collection(PLAN_COLLECTION)


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
                    val subItems = (subSnapshot?.documents?.mapNotNull { it.toTransaction } ?: emptyList())
                        .map { fulfillment ->
                            when (fulfillment) {
                                is Transaction.Achievement -> fulfillment.copy(goal = transaction as Transaction.Goal)
                                is Transaction.Attain -> fulfillment.copy(goal = transaction as Transaction.Goal)
                                is Transaction.PlanFulfillment -> fulfillment.copy(plan = transaction as Transaction.Plan)
                                else -> fulfillment
                            }
                        }
                    trySend(Result.success(subItems))
                }
            awaitClose { subListener.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>> {
        return callbackFlow {
            val transactionsCollection = docRef.document(userId).collection(TRANSACTION_COLLECTION)
            val subListeners = mutableMapOf<String, List<ListenerRegistration>>()
            val fulfillmentMap = mutableMapOf<String, MutableMap<String, List<Transaction>>>()
            var latestTransactions = emptyList<Transaction>()

            fun emitMerged() {
                val merged = latestTransactions.map { transaction ->
                    val collectionsMap = fulfillmentMap[transaction.id]
                    val subItems = collectionsMap?.values?.flatten() ?: emptyList()
                    when (transaction) {
                        is Transaction.Lent -> {
                            val repayment = subItems.filterIsInstance<Transaction.Repayment>()
                                .map { it.copy(lent = transaction) }
                            transaction.copy(
                                repayment = repayment
                            )
                        }
                        is Transaction.Debt -> {
                            val settlement = subItems.filterIsInstance<Transaction.Settlement>()
                                .map { it.copy(debt = transaction) }

                            transaction.copy(settlement = settlement)
                        }
                        is Transaction.Goal -> {
                            val attain = subItems.filterIsInstance<Transaction.Attain>()
                                .map { it.copy(goal = transaction) }
                            val achievement = subItems.filterIsInstance<Transaction.Achievement>()
                                .map { it.copy(goal = transaction) }

                            transaction.copy(attain = attain, achievement = achievement)
                        }
                        is Transaction.Plan -> {
                            val transactions = subItems.filterIsInstance<Transaction.PlanFulfillment>()
                                .map { it.copy(plan = transaction) }
                            transaction.copy(transactions = transactions)
                        }
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
                    subListeners.remove(id)?.forEach { it.remove() }
                    fulfillmentMap.remove(id)
                }

                // Add new listeners for added transactions
                newTransactions.forEach { transaction ->
                    if (!subListeners.containsKey(transaction.id)) {
                        val collectionsMap = mutableMapOf<String, List<Transaction>>()
                        fulfillmentMap[transaction.id] = collectionsMap

                        val collectionsToListen = when (transaction) {
                            is Transaction.Lent -> listOf(REPAYMENT_COLLECTION)
                            is Transaction.Debt -> listOf(SETTLEMENT_COLLECTION)
                            is Transaction.Goal -> listOf(ATTAIN_COLLECTION, ACHIEVEMENT_COLLECTION)
                            is Transaction.Plan -> listOf(PLAN_COLLECTION)
                            else -> emptyList()
                        }

                        val listeners = collectionsToListen.map { collectionName ->
                            collectionsMap[collectionName] = emptyList()
                            val subCollection = transactionsCollection.document(transaction.id).collection(collectionName)
                            subCollection.addSnapshotListener { subSnapshot, subError ->
                                if (subError == null) {
                                    collectionsMap[collectionName] = subSnapshot?.documents?.mapNotNull { it.toTransaction } ?: emptyList()
                                    emitMerged()
                                }
                            }
                        }
                        subListeners[transaction.id] = listeners
                    }
                }

                latestTransactions = newTransactions
                emitMerged()
            }

            awaitClose {
                topListener.remove()
                subListeners.values.flatten().forEach { it.remove() }
            }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }
}
