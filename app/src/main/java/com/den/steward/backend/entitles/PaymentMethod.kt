package com.den.steward.backend.entitles

import com.den.steward.R

enum class PaymentMethod(val icon: Int, val label: String) {
    CASH(R.drawable.cash, "Cash"),
    CARD(R.drawable.credit_card, "Credit Card")
}