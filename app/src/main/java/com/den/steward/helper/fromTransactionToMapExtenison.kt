package com.den.steward.helper

import com.den.steward.backend.dataStructure.RecurrencePattern
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.dataStructure.Transaction.Achievement
import com.den.steward.backend.dataStructure.Transaction.Attain
import com.den.steward.backend.dataStructure.Transaction.Debt
import com.den.steward.backend.dataStructure.Transaction.Earnings
import com.den.steward.backend.dataStructure.Transaction.Expense
import com.den.steward.backend.dataStructure.Transaction.Goal
import com.den.steward.backend.dataStructure.Transaction.Loan
import com.den.steward.backend.dataStructure.Transaction.Refund
import com.den.steward.backend.dataStructure.Transaction.Repayment
import com.den.steward.backend.dataStructure.Transaction.Savings
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

val Transaction.toMap: MutableMap<String, Any>
    get() {
        val mapping = mutableMapOf<String, Any>(
            "id" to this.id,
            "type" to this.type.name,
            "createdAt" to if (this.createdAt < System.currentTimeMillis() - 1000) {
                Timestamp(java.util.Date(this.createdAt))
            } else {
                FieldValue.serverTimestamp()
            }
        )

        when(this) {
            is Earnings -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Expense -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Loan -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Debt -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Savings -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Goal -> {
                mapping["value"] = this.value
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["goalType"] = this.goalType.name
                mapping["status"] = this.status.name
                mapping["repeatable"] = this.repeatable.name
                if (this.repeatable is RecurrencePattern.Custom) {
                    mapping["repeatableDays"] = this.repeatable.days
                }
                mapping["startedAt"] = Timestamp(java.util.Date(this.startedAt))
                mapping["endAt"] = Timestamp(java.util.Date(this.endAt))
            }

            is Attain -> {
                mapping["value"] = this.value
            }

            is Repayment -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Refund -> {
                mapping["amount"] = this.amount
                mapping["label"] = this.label
                mapping["note"] = this.note
                mapping["paymentMethod"] = this.paymentMethod.name
                mapping["affectAmount"] = this.affectAmount
            }

            is Achievement -> {
                mapping["value"] = this.value
                mapping["startAt"] = Timestamp(java.util.Date(this.startAt))
                mapping["endAt"] = Timestamp(java.util.Date(this.endAt))
            }
        }
        return mapping
    }
