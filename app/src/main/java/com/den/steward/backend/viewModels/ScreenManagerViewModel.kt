package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    authorizationUseCase: AuthorizationUseCase
) : ViewModel() {
    val userState: StateFlow<AuthState> = authorizationUseCase.userState
}