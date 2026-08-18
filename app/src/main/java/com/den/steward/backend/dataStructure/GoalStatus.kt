package com.den.steward.backend.dataStructure

import com.den.steward.R

enum class GoalStatus(
    val label: String,
    val color: Int
) {
    NOT_STARTED(
        "Not Started",
        R.color.not_started
    ),
    IN_PROGRESS(
        "In Progress",
        R.color.in_progress
    ),
    COMPLETED(
        "Completed",
        R.color.completed
    ),
    FAILED(
        "Failed",
        R.color.failed
    )
}