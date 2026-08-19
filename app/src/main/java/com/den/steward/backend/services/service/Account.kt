package com.den.steward.backend.services.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.den.steward.backend.states.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.StateFlow

interface Account {
    val auth: FirebaseAuth
    val userState: StateFlow<AuthState>
    val currentUserId: String
    val hasUser: Boolean


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun handleGoogleSignIn(context: Context): Exception?
    suspend fun login(email: String, password: String)
    suspend fun register(firstName: String, lastName: String, email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAnonymousAccount()
    suspend fun linkAccount(email: String, password: String)
    suspend fun deleteAccount()
    fun updateState(authState: AuthState)
    suspend fun signOut()
}