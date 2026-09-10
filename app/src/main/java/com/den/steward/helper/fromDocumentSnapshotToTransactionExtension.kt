// Grace and truth came through JESUS
package com.den.steward.helper

import android.util.Log
import com.den.steward.R
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.GoalType
import com.den.steward.backend.entitles.LiabilitiesStatus
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.google.firebase.firestore.DocumentSnapshot

private val paymentMethodMap = PaymentMethod.entries.associateBy { it.name }
private val goalTypeMap = GoalType.entries.associateBy { it.name }
private val goalStatusMap = GoalStatus.entries.associateBy { it.name }
private val recurrencePatternMap = RecurrencePattern.entries.associateBy { it.name }
private val liabilitiesStatusMap = LiabilitiesStatus.entries.associateBy { it.name }

val DocumentSnapshot.toTransaction: Transaction?
    get() {
    val id = getString("id").let { if (it.isNullOrEmpty()) this.id else it }
    val label = getString("label") ?: ""
    val type = getString("type") ?: ""
    val note = getString("note") ?: ""
    val createdAt = getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
    val affectAmount = getBoolean("affectAmount") ?: false
    val amount = getDouble("amount") ?: 0.0
    val paymentMethod = getString("paymentMethod")?.let { name ->
        paymentMethodMap[name]
    } ?: PaymentMethod.CASH
    val selectedIcon = getLong("selectedIcon") ?: R.drawable.description.toLong()

    return when (type) {
        TransactionType.GOAL.name -> {
            val value = getDouble("value") ?: 0.0
            val goalType = getString("goalType")?.let { name ->
                goalTypeMap[name]
            } ?: GoalType.AMOUNT
            val status = getString("status")?.let { name ->
                goalStatusMap[name]
            } ?: GoalStatus.NOT_STARTED
            val startedAt = getTimestamp("startedAt")?.toDate()?.time ?: createdAt
            val endAt = getTimestamp("endAt")?.toDate()?.time ?: createdAt
            val repeatable = when (val repeatableName = getString("repeatable")) {
                "Custom" -> {
                    val days = (get("repeatableDays") as? List<*>)?.filterIsInstance<Long>() ?: emptyList()
                    RecurrencePattern.Custom(days.map { it.toInt() })
                }
                else -> recurrencePatternMap[repeatableName] ?: RecurrencePattern.NONE
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
                selectedIcon = selectedIcon.toInt()
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
                goalStatusMap[name]
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

        TransactionType.EARNINGS.name -> {
            Transaction.Earnings(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
                selectedIcon = selectedIcon.toInt()
            )
        }

        TransactionType.EXPENSE.name -> {
            Transaction.Expense(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
                selectedIcon = selectedIcon.toInt()
            )
        }

        TransactionType.SAVINGS.name -> {
            Transaction.Savings(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
                selectedIcon = selectedIcon.toInt()
            )
        }

        TransactionType.LENT.name -> {
            val liabilitiesStatus = getString("liabilitiesStatus")?.let { name ->
                liabilitiesStatusMap[name]
            } ?: LiabilitiesStatus.UNPAID

            Transaction.Lent(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
                selectedIcon = selectedIcon.toInt(),
                liabilitiesStatus = liabilitiesStatus
            )
        }

        TransactionType.DEBT.name -> {
            val liabilitiesStatus = getString("liabilitiesStatus")?.let { name ->
                liabilitiesStatusMap[name]
            } ?: LiabilitiesStatus.UNPAID

            Transaction.Debt(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
                selectedIcon = selectedIcon.toInt(),
                liabilitiesStatus = liabilitiesStatus
            )
        }

        TransactionType.REPAYMENT.name -> {
            Transaction.Repayment(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
            )
        }

        TransactionType.REFUND.name -> {
            Transaction.Refund(
                id = id,
                label = label,
                amount = amount,
                note = note,
                createdAt = createdAt,
                paymentMethod = paymentMethod,
                affectAmount = affectAmount,
            )
        }

        else -> {
            Log.w("ToTransaction", "Unknown transaction type '$type' on document ${this.id}, dropping")
            null
        }
    }
}
