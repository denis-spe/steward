package com.den.steward.backend.viewModels

import app.cash.turbine.test
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authorizationUseCase: AuthorizationUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val userStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        every { authorizationUseCase.userState } returns userStateFlow
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authorizationUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should set isLoading to true then false`() = runTest {
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            viewModel.login("test@example.com", "password")
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
        }

        coVerify { authorizationUseCase.login("test@example.com", "password") }
    }

    @Test
    fun `updateAuthState should call useCase`() {
        val newState = AuthState.NotAuthenticated
        viewModel.updateAuthState(newState)
        coVerify { authorizationUseCase.updateAuthState(newState) }
    }
}
