package com.den.steward.backend.dataStructure

import androidx.compose.ui.graphics.Color
import com.den.steward.R

enum class TransactionType(
    val label: Int,
    val description: Int,
    val icon: Int,
    val color: Int
) {
    EARNINGS(
        R.string.earning,
        R.string.earning_desc,
        R.drawable.ic_earnings,
        R.color.earnings
    ),
    EXPENSE(
        R.string.expense,
        R.string.expense_desc,
        R.drawable.ic_expense,
        R.color.expense
    ),

    LOAN(
        R.string.loan,
        R.string.loan_desc,
        R.drawable.ic_loan,
        R.color.loan
    ),

    DEBT(
        R.string.debt,
        R.string.debt_desc,
        R.drawable.ic_debt,
        R.color.debt
    ),

    REPAYMENT(
        R.string.repayment,
        R.string.repayment_desc,
        R.drawable.ic_repayment,
        R.color.repayment
    ),

    REFUND(
        R.string.refund,
        R.string.refund_desc,
        R.drawable.ic_refund,
        R.color.refund
    ),

    GOAL(
        R.string.goal,
        R.string.goal_desc,
        R.drawable.ic_finance_target,
        R.color.goal
    ),

    ATTAIN(
        R.string.attain,
        R.string.attain_desc,
        R.drawable.ic_attain,
        R.color.attain
    ),

    ACHIEVEMENT(
        R.string.achievement,
        R.string.achievement_desc,
        R.drawable.ic_achievement,
        R.color.achievement
    ),

    SAVINGS(
        R.string.savings,
        R.string.savings_desc,
        R.drawable.ic_savings,
        R.color.savings
    )
}