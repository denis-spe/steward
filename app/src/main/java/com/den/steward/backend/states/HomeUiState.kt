package com.den.steward.backend.states

data class HomeUiState(
    val currentTab: HomeTab = HomeTab.TODAY,
    val allTabFilter: Filter? = null
)
