package com.den.steward.backend.dataStructure

import androidx.compose.runtime.Stable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue

sealed class Transaction {
    abstract val id: String
    abstract val type: String
    abstract val label: String
    abstract val note: String

    abstract val createdAt: Long
    @Stable
    data class Earning(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "earnings",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Expense(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "expense",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Loan(
       override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "loan",
        @get:Exclude
        val repayment: List<Repayment> = emptyList(),
        @get:Exclude
        val totalRepayment: Double = repayment.sumOf { it.amount },
        @get:Exclude
        val remainingAmount: Double = amount - totalRepayment,
       override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Debt(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "debt",
        @get:Exclude
        val refund: List<Refund> = emptyList(),
        @get:Exclude
        val totalRefund: Double = refund.sumOf { it.amount },
        @get:Exclude
        val remainingAmount: Double = amount - totalRefund,
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()


    @Stable
    data class Repayment(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "repayment",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Refund(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "refund",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class TargetAmount(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "targetAmount",
        @get:Exclude
        val attain: List<TargetAttain> = emptyList(),
        @get:Exclude
        val remainingAmount: Double = amount - attain.sumOf { it.amount },
        @get:Exclude
        val totalAmount: Double = attain.sumOf { it.amount },
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class CountTarget(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val count: Long = 0L,
        override val type: String = "countTarget",
        @get:Exclude
        val attain: List<CountTargetAttain> = emptyList(),
        @get:Exclude
        val remainingCount: Long = count - attain.sumOf { it.count },
        @get:Exclude
        val totalCount: Long = attain.sumOf { it.count },
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class TargetAttain(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val amount: Double = 0.0,
        override val type: String = "targetAttain",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class CountTargetAttain(
        override val id: String = "",
        override val label: String = "",
        override val note: String = "",
        val count: Long = 0L,
        override val type: String = "countTargetAttain",
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    val toMap: MutableMap<String, Any>
        get() {
            val mapping = mutableMapOf<String, Any>(
                "id" to this.id,
                "label" to this.label,
                "note" to this.note,
                "type" to this.type,
                // Use the object's createdAt if it's not "now" (useful for testing/backdating)
                // Otherwise let Firestore set the precise server time.
                "createdAt" to if (this.createdAt < System.currentTimeMillis() - 1000) {
                    Timestamp(java.util.Date(this.createdAt))
                } else {
                    FieldValue.serverTimestamp()
                }
            )

            when(this) {
                is Earning -> {
                    mapping["amount"] = this.amount
                }

                is Expense -> {
                    mapping["amount"] = this.amount
                }

                is Loan -> {
                    mapping["amount"] = this.amount
                }

                is Debt -> {
                    mapping["amount"] = this.amount
                }

                is TargetAmount -> {
                    mapping["amount"] = this.amount
                }

                is TargetAttain -> {
                    mapping["amount"] = this.amount
                }

                is CountTarget -> {
                    mapping["count"] = this.count
                }

                is CountTargetAttain -> {
                    mapping["count"] = this.count
                }

                is Repayment -> {
                    mapping["amount"] = this.amount
                }

                is Refund -> {
                    mapping["amount"] = this.amount
                }
            }
            return mapping
        }
}