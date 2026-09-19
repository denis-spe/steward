package com.den.steward.backend.entitles

import com.den.steward.R

enum class PlanStatus(
    val label: String,
    val color: Int
) {
    NOT_YET("Not Yet", R.color.not_started),
    ACHIEVED("Achieved", R.color.completed),
    FAILED("Failed", R.color.failed),
    PENDING("Pending", R.color.in_progress)
}
