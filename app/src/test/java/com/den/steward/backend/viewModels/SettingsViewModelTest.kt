package com.den.steward.backend.viewModels

import com.den.steward.backend.states.AuthState
import com.den.steward.backend.useCase.AuthorizationUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val authorizationUseCase: AuthorizationUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val userStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        every { authorizationUseCase.userState } returns userStateFlow
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(authorizationUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logout should call useCase signOutUseCase`() = runTest {
        viewModel.logout()
        advanceUntilIdle()
        coVerify { authorizationUseCase.signOutUseCase() }
    }
}
