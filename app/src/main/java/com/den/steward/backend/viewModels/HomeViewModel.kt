package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.states.DataNotifier
import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.states.HomeUiState
import com.den.steward.backend.useCase.AuthorizationUseCase
import com.den.steward.backend.states.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    authorizationUseCase: AuthorizationUseCase,
) : ViewModel(){
    val userState: StateFlow<AuthState> = authorizationUseCase.userState
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    fun updateHomeTab(tab: HomeTab, filter: Filter? = null) {
        _homeUiState.value = _homeUiState.value.copy(currentTab = tab, allTabFilter = filter)
    }

    fun clearAllTabFilter() {
        _homeUiState.value = _homeUiState.value.copy(allTabFilter = null)
    }

    fun updateDataNotifier(dataNotifier: DataNotifier?) {
        _homeUiState.update { it.copy(dataNotifier = dataNotifier) }
        if (dataNotifier != null) {
            viewModelScope.launch {
                delay(4000.milliseconds)
                _homeUiState.update { it.copy(dataNotifier = null) }
            }
        }
    }
}
