package com.den.steward.backend.states

import androidx.compose.runtime.Immutable

@Immutable
sealed class AuthState {
    @Immutable
    data object Loading : AuthState()

    @Immutable
    data object NotAuthenticated : AuthState()

    @Immutable
    data class Authenticated(
        val userId: String,
        val displayName: String? = null,
        val email: String? = null,
        val photoUrl: String? = null,
        val isAnonymous: Boolean
    ) : AuthState()

    @Immutable
    data class Error(val message: String) : AuthState()

    @Immutable
    data class Success(val message: String) : AuthState()
}