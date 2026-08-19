// Grace and truth came through JESUS
package com.den.steward.helper

import android.util.Log
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.GoalType
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.google.firebase.firestore.DocumentSnapshot

// Updated factory to include affectAmount and other basic flags
private typealias TransactionFactory = (id: String, label: String, amount: Double, note: String, createdAt: Long, paymentMethod: PaymentMethod, affectAmount: Boolean) -> Transaction

private val amountFactories: Map<String, TransactionFactory> = mapOf(
    TransactionType.EARNINGS.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Earnings(id, label, note, amount, TransactionType.EARNINGS, createdAt, paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.EXPENSE.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Expense(id, label, note, amount, TransactionType.EXPENSE, createdAt, paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.LENT.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Lent(id, label, note, amount, TransactionType.LENT, emptyList(), createdAt, paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.DEBT.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Debt(id, label, note, amount, TransactionType.DEBT, emptyList(), createdAt, paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.REPAYMENT.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Repayment(id, label, note, amount, TransactionType.REPAYMENT, createdAt, lent = Transaction.Lent(), paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.REFUND.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Refund(id, label, note, amount, TransactionType.REFUND, createdAt, debt = Transaction.Debt(), paymentMethod = paymentMethod, affectAmount = affect) },
    TransactionType.SAVINGS.name to { id, label, amount, note, createdAt, paymentMethod, affect -> Transaction.Savings(id, label, note, amount, TransactionType.SAVINGS, createdAt, paymentMethod = paymentMethod, affectAmount = affect) },
)

val DocumentSnapshot.toTransaction: Transaction?
    get() {
    val id = getString("id").let { if (it.isNullOrEmpty()) this.id else it }
    val label = getString("label") ?: ""
    val type = getString("type") ?: ""
    val note = getString("note") ?: ""
    val createdAt = getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
    val affectAmount = getBoolean("affectAmount") ?: false

    amountFactories[type]?.let { factory ->
        val amount = getDouble("amount") ?: 0.0
        val paymentMethod = getString("paymentMethod")?.let { name ->
            PaymentMethod.entries.find { it.name == name }
        } ?: PaymentMethod.CASH
        return factory(id, label, amount, note, createdAt, paymentMethod, affectAmount)
    }

    return when (type) {
        TransactionType.GOAL.name -> {
            val value = getDouble("value") ?: 0.0
            val goalType = getString("goalType")?.let { name ->
                GoalType.entries.find { it.name == name }
            } ?: GoalType.AMOUNT
            val status = getString("status")?.let { name ->
                GoalStatus.entries.find { it.name == name }
            } ?: GoalStatus.NOT_STARTED
            val startedAt = getTimestamp("startedAt")?.toDate()?.time ?: createdAt
            val endAt = getTimestamp("endAt")?.toDate()?.time ?: createdAt
            val repeatable = when (val repeatableName = getString("repeatable")) {
                "Custom" -> {
                    val days = (get("repeatableDays") as? List<*>)?.filterIsInstance<Long>() ?: emptyList()
                    RecurrencePattern.Custom(days.map { it.toInt() })
                }
                else -> RecurrencePattern.entries.find { it.name == repeatableName } ?: RecurrencePattern.NONE
            }

            Transaction.Goal(
                id = id,
                label = label,
                value = value,
                note = note,
                goalType = goalType,
                status = status,
                startedAt = startedAt,
                endAt = endAt,
                createdAt = createdAt,
                repeatable = repeatable,
            ).calculateStatus(System.currentTimeMillis())
        }

        TransactionType.ATTAIN.name -> {
            val value = getDouble("value") ?: 0.0
            Transaction.Attain(id = id, value = value, createdAt = createdAt)
        }

        TransactionType.ACHIEVEMENT.name -> {
            val value = getDouble("value") ?: 0.0
            val startAt = getTimestamp("startAt")?.toDate()?.time ?: createdAt
            val endAt = getTimestamp("endAt")?.toDate()?.time ?: createdAt
            val status = getString("status")?.let { name ->
                GoalStatus.entries.find { it.name == name }
            } ?: GoalStatus.NOT_STARTED
            Transaction.Achievement(
                id = id,
                value = value,
                createdAt = createdAt,
                startAt = startAt,
                endAt = endAt,
                status = status
            )
        }

        else -> {
            Log.w("ToTransaction", "Unknown transaction type '$type' on document ${this.id}, dropping")
            null
        }
    }
}
