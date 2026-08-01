package com.den.steward.backend.viewModels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase
) : ViewModel(){
    val userState: StateFlow<AuthState> = authorizationUseCase.userState
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onAnonymousLogin() {
        viewModelScope.launch {
            _isLoading.value = true
            authorizationUseCase.createAnonymousAccountUseCase()
            _isLoading.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun onGoogleSignIn(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            authorizationUseCase.googleAuthUseCase(context)
            _isLoading.value = false
        }
    }

    fun updateAuthState(authState: AuthState) {
        authorizationUseCase.updateAuthState(authState)
    }
}