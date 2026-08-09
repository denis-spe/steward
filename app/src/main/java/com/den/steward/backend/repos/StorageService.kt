package com.den.steward.backend.repos

import android.util.Log
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.backend.repoInterfaces.Storage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
    }

    private val docRef = firestore
        .collection(USER_COLLECTION)

    // Maps a transaction "type" string to a factory that only needs id/label/note/createdAt plus
    // a single "amount" field. Uses named-argument lambdas (not bound constructor references) so
    // this compiles regardless of each subtype's actual declared parameter order.
    private val amountFactories: Map<String, (id: String, label: String, amount: Double, note: String, createdAt: Long) -> Transaction> = mapOf(
        TransactionType.EARNINGS.name to { id, label, amount, note, createdAt -> Transaction.Earning(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
        TransactionType.EXPENSE.name to { id, label, amount, note, createdAt -> Transaction.Expense(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
        TransactionType.LOAN.name to { id, label, amount, note, createdAt -> Transaction.Loan(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
        TransactionType.DEBT.name to { id, label, amount, note, createdAt -> Transaction.Debt(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
        TransactionType.REPAYMENT.name to { id, label, amount, note, createdAt -> Transaction.Repayment(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
        TransactionType.REFUND.name to { id, label, amount, note, createdAt -> Transaction.Refund(id = id, label = label, amount = amount, note = note, createdAt = createdAt) },
    )

    fun transactionCollectionRef(transaction: Transaction) {

    }

    fun DocumentSnapshot.toDTO(): Transaction? {
        val id = getString("id").let { if (it.isNullOrEmpty()) this.id else it }
        val label = getString("label") ?: ""
        val type = getString("type") ?: ""
        val note = getString("note") ?: ""
        val createdAt = getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()

        amountFactories[type]?.let { factory ->
            val amount = getDouble("amount") ?: 0.0
            return factory(id, label, amount, note, createdAt)
        }

        return when (type) {
            TransactionType.EARNINGS.name -> {
                val value = getDouble("value") ?: 0.0
                Transaction.Goal(id = id, label = label, value = value, note = note, createdAt = createdAt)
            }

            TransactionType.ATTAIN.name -> {
                val value = getDouble("value") ?: 0.0
                Transaction.Attain(id = id, value = value, createdAt = createdAt)
            }

            TransactionType.ACHIEVEMENT.name -> {
                val value = getDouble("value") ?: 0.0
                Transaction.Achievement(id = id, value = value, createdAt = createdAt)
            }

            else -> {
                Log.w(TAG, "Unknown transaction type '$type' on document ${this.id}, dropping")
                null
            }
        }
    }

    override suspend fun addTransaction(userId: String, transaction: Transaction): Result<Unit> {
        return try {
            val transactionRef = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .document()

            val transactionData = transaction.toMap
            transactionData["id"] = transactionRef.id

            transactionRef.set(transactionData).await()
            Result.success(Unit)
        } catch (e: Exception) {
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
            transactionRef.collection(collection)
                .add(fulfillmentData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add fulfillment for transaction $transactionId (user $userId)", e)
            Result.failure(e)
        }
    }

    override fun fetchTransactionFulfillment(userId: String, transaction: Transaction): Flow<Result<List<Transaction>>> {
        val collection = when (transaction) {
            is Transaction.Loan -> docRef.document(userId)
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
                    val subItems = subSnapshot?.documents?.mapNotNull { it.toDTO() } ?: emptyList()
                    trySend(Result.success(subItems))
                }
            awaitClose { subListener.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>> {
        // Raw top-level listener, fires on ANY change to ANY transaction doc (add, remove, or a
        // field edit like label/amount). Shared (not re-collected) so its two consumers below
        // don't each open a separate Firestore listener on the same query.
        val topLevelFlow: Flow<Result<List<Transaction>>> = callbackFlow {
            val listener = docRef.document(userId)
                .collection(TRANSACTION_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Top-level transaction listener failed for user $userId", error)
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }

                    val transactions = snapshot?.documents?.mapNotNull { it.toDTO() } ?: emptyList()
                    trySend(Result.success(transactions))
                }

            awaitClose {
                listener.remove()
            }
        }

        // Only rebuilds the sub-collection listeners when the *set* of transaction ids changes.
        // distinctUntilChangedBy filters out emissions that only touch a transaction's own
        // top-level fields, so those no longer tear down and race the sub-listeners below before
        // they get a chance to emit.
        val subItemsFlow: Flow<Result<Map<String, List<Transaction>>>> = topLevelFlow
            .distinctUntilChangedBy { result -> result.getOrNull()?.map { it.id }?.sorted() ?: emptyList() }
            .flatMapLatest { result ->
                if (result.isFailure) {
                    return@flatMapLatest flowOf(Result.failure<Map<String, List<Transaction>>>(result.exceptionOrNull()!!))
                }

                val transactions = result.getOrNull() ?: emptyList()
                if (transactions.isEmpty()) return@flatMapLatest flowOf(Result.success(emptyMap()))

                // Each flow now carries Result<Transaction> instead of a bare Transaction, so a
                // failure in any single subcollection listener is no longer silently downgraded
                // to "no items" — it propagates out of fetchAllTransactions instead.
                //
                // Each per-transaction flow also starts with an immediate "no sub-items yet"
                // placeholder via onStart. Without this, combine() below waits for EVERY
                // sub-collection listener to deliver its first snapshot before emitting
                // anything at all. If the device is offline and even one sub-collection has
                // no local cache yet (e.g. a Loan/Debt/Goal created while offline, before
                // Firestore ever synced that subcollection), that single stalled listener
                // used to block the entire transaction list from displaying — including
                // transactions that had nothing to do with it and were already cached.
                val perTransactionFlows: List<Flow<Pair<String, Result<List<Transaction>>>>> = transactions.map { transaction ->
                    fetchTransactionFulfillment(userId, transaction)
                        .map { subResult -> transaction.id to subResult }
                        .onStart { emit(transaction.id to Result.success(emptyList())) }
                }

                combine(perTransactionFlows) { pairs ->
                    val failure = pairs.map { it.second }.firstOrNull { it.isFailure }
                    if (failure != null) {
                        Result.failure(failure.exceptionOrNull() ?: IllegalStateException("Unknown fulfillment error"))
                    } else {
                        Result.success(pairs.associate { (id, subResult) -> id to (subResult.getOrNull() ?: emptyList()) })
                    }
                }
            }

        // Merge: latest top-level fields (fresh on every edit) with latest known sub-items per id
        // (fresh only when the id set actually changes, or when that specific transaction's own
        // sub-listener fires).
        return combine(topLevelFlow, subItemsFlow) { topLevelResult, subItemsResult ->
            when {
                topLevelResult.isFailure -> topLevelResult
                subItemsResult.isFailure -> Result.failure(subItemsResult.exceptionOrNull()!!)
                else -> {
                    val subItemsById = subItemsResult.getOrNull() ?: emptyMap()
                    val merged = (topLevelResult.getOrNull() ?: emptyList()).map { transaction ->
                        val subItems = subItemsById[transaction.id] ?: emptyList()
                        when (transaction) {
                            is Transaction.Loan -> transaction.copy(repayment = subItems.filterIsInstance<Transaction.Repayment>())
                            is Transaction.Debt -> transaction.copy(refund = subItems.filterIsInstance<Transaction.Refund>())
                            is Transaction.Goal -> transaction.copy(attain = subItems.filterIsInstance<Transaction.Attain>())
                            else -> transaction
                        }
                    }
                    Result.success(merged)
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}