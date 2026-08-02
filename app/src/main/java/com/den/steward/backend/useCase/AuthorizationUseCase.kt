// Bless be the name of LORD GOD of hosts
package com.den.steward.backend.useCase

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.states.AuthState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val accountService: Account
) {
    val userState: StateFlow<AuthState> = accountService.userState

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun googleAuthUseCase(
        context: Context,
    ) {
        accountService.handleGoogleSignIn(context)
    }

    suspend fun signOutUseCase() {
        accountService.signOut()
    }

    suspend fun createAnonymousAccountUseCase() {
        accountService.createAnonymousAccount()
    }

    fun updateAuthState(authState: AuthState){
        accountService.updateState(authState)
    }

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        password: String,
        email: String,
    ) {
        accountService.register(
            firstName,
            lastName = lastName,
            password = password,
            email = email
        )
    }

    suspend fun login(
        email: String,
        password: String,
    ) {
        accountService.login(
            email = email,
            password = password
        )
    }

    suspend fun sendPasswordResetEmail(email: String) {
        accountService.sendRecoveryEmail(email)
    }
}