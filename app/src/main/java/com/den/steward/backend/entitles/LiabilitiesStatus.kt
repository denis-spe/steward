package com.den.steward.backend.entitles

import com.den.steward.R

enum class LiabilitiesStatus(val label: String, val color: Int) {
    PAYING("paying", R.color.paying), PAID("paid", R.color.paid), UNPAID("unpaid", R.color.unpaid)
}
