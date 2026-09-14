package com.den.steward.backend.states

import com.den.steward.backend.useCase.Filter

data class HomeUiState(
    val currentTab: HomeTab = HomeTab.TODAY,
    val allTabFilter: Filter? = null
)
