package com.den.steward.backend.viewModels

import app.cash.turbine.test
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
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
class HomeViewModelTest {

    private val authorizationUseCase: AuthorizationUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val userStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        every { authorizationUseCase.userState } returns userStateFlow
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(authorizationUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `userState should reflect useCase userState`() = runTest {
        viewModel.userState.test {
            assertEquals(AuthState.Loading, awaitItem())
            userStateFlow.value = AuthState.NotAuthenticated
            assertEquals(AuthState.NotAuthenticated, awaitItem())
        }
    }
}
