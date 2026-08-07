// Bless be to the LORD GOD of hosts
package com.den.steward.helper

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.dataStructure.Transaction

val String.title: String
    get() {
        return this.replaceFirstChar { it.uppercase() }
    }

val String.isEmailValid: String?
    get() {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")

        return if (this.isBlank() || this.isEmpty()) {
            "Email cannot be blank"
        } else if (!emailRegex.matches(this)) {
            "Invalid email format"
        } else {
            null
        }
    }

val String.isPasswordValid: String?
    get() {
        // Example password validation (at least 6 characters)
        return if (this.isBlank() && this.isEmpty()) {
            "Password cannot be empty"
        } else if (this.length < 8) {
            "Password must be at least 8 characters long"
        } else {
            null
        }
    }

val String.isNameValid: String?
    get() {
        return if (this.isBlank() || this.isEmpty()) {
            "Name cannot be blank"
        } else {
            null
        }
    }

fun NavBackStack<NavKey>.pop() {
    if (this.size > 1) {
        this.removeLastOrNull()
    }
}

val Any.toTransaction: List<Transaction>
    get() {
        val raw = this as List<*>
        val transaction = raw.filterIsInstance<Map<String, Any>>()
        return transaction.map {
            it as Transaction
        }
    }
