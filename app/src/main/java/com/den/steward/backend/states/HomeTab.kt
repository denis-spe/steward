package com.den.steward.backend.states

enum class HomeTab(val label: String, val idx: Int) {
    OVERVIEW("Overview", 0),
    TODAY("Today", 1),
    YESTERDAY("Yesterday", 2),
    PLAN("Plan", 3),
    ALL("All", 4),
}