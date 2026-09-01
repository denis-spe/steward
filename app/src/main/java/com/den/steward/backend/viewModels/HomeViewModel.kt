package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.states.HomeUiState
import com.den.steward.backend.useCase.AuthorizationUseCase
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authorizationUseCase: AuthorizationUseCase,
) : ViewModel(){
    val userState: StateFlow<AuthState> = authorizationUseCase.userState
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    fun updateHomeTab(tab: HomeTab) {
        _homeUiState.value = _homeUiState.value.copy(currentTab = tab)
    }
}