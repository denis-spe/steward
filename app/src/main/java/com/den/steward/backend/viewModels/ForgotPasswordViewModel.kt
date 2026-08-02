package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase
) : ViewModel() {
    val userState = authorizationUseCase.userState
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authorizationUseCase.sendPasswordResetEmail(email)
            _isLoading.value = false
        }
    }

    fun updateAuthState(authState: AuthState) {
        authorizationUseCase.updateAuthState(authState)
    }
}