// Glory be the name of LORD GOD
package com.den.steward.ui.screens.screenManager

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.ForgotPasswordViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.backend.viewModels.LoginViewModel
import com.den.steward.backend.viewModels.RegisterViewModel
import com.den.steward.backend.viewModels.ScreenManagerViewModel
import com.den.steward.backend.viewModels.SettingsViewModel
import com.den.steward.backend.viewModels.WelcomeViewModel
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.forgotPasswordScreen.ForgotPasswordScreen
import com.den.steward.ui.screens.authScreen.loginScreen.LoginScreen
import com.den.steward.ui.screens.authScreen.registerScreen.EmailScreen
import com.den.steward.ui.screens.authScreen.registerScreen.NameScreen
import com.den.steward.ui.screens.authScreen.registerScreen.PasswordScreen
import com.den.steward.ui.screens.homeScreen.HomeScreen
import com.den.steward.ui.screens.loadingScreen.LoadingScreen
import com.den.steward.ui.screens.settingsScreen.SettingsScreen
import com.den.steward.ui.screens.welcomeScreen.WelcomeScreen

fun EntryProviderScope<NavKey>.featureAEntryBuilder(
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel,
    welcomeViewModel: WelcomeViewModel,
) {
    // ===== Welcome Screen =====
    entry<WelcomeRouter> {
        WelcomeScreen(
            backStack = backStack,
            welcomeViewModel = welcomeViewModel,
        )
    }

    // ===== Email Screen =====
    entry<EmailRouter> {
        EmailScreen(
            backStack = backStack,
            registerViewModel = registerViewModel
        )
    }

    // ===== Name Screen =====
    entry<NameRouter> {
        NameScreen(
            backStack = backStack,
            registerViewModel = registerViewModel
        )
    }

    // ===== Password Screen =====
    entry<PasswordRouter> {
        PasswordScreen(
            backStack = backStack,
            registerViewModel = registerViewModel
        )
    }

    // ===== Login Screen =====
    entry<LoginRouter> {
        val loginViewModel: LoginViewModel = hiltViewModel()
        LoginScreen(
            backStack = backStack,
            loginViewModel = loginViewModel
        )
    }

    // ===== Home Screen =====
    entry<HomeRouter> {
        val dataAdditionViewModel: DataAdditionViewModel = hiltViewModel()
        val dataFetchViewModel: DataFetchViewModel = hiltViewModel()
        val homeViewModel: HomeViewModel = hiltViewModel()

        HomeScreen(
            backStack = backStack,
            homeViewModel = homeViewModel,
            dataAdditionViewModel = dataAdditionViewModel,
            dataFetchViewModel = dataFetchViewModel
        )
    }

    // ===== Settings Screen =====
    entry<SettingsRouter> {
        val settingsViewModel: SettingsViewModel = hiltViewModel()
        SettingsScreen(
            backStack = backStack,
            settingsViewModel = settingsViewModel
        )
    }

    // ===== Forgot Password Screen =====
    entry<ForgotPasswordRouter> {
        val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
        ForgotPasswordScreen(
            backStack = backStack,
            forgotPasswordViewModel = forgotPasswordViewModel
        )
    }
}

@Composable
fun ScreenManager() {
    // 1. Instantiate the viewModel
    val viewModel: ScreenManagerViewModel = hiltViewModel()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val welcomeViewModel: WelcomeViewModel = hiltViewModel()

    // 2. Observe the userState
    val userState by viewModel.userState.collectAsStateWithLifecycle()

    if (userState is AuthState.Loading) {
        LoadingScreen()
        return
    }

    // 3. Check if the user is logged in or not
    val isLogIn = remember(userState) {
        when(userState) {
            AuthState.NotAuthenticated -> WelcomeRouter
            is AuthState.Authenticated -> HomeRouter
            is AuthState.Error -> WelcomeRouter
            else -> WelcomeRouter // Should not happen due to early return
        }
    }

    // 4. Create the NavBackStack with the initial screen
    val backStack = key(isLogIn) {
        rememberNavBackStack(isLogIn)
    }


    // 5. Create the NavDisplay
    NavDisplay(
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.pop() },
        entryProvider = entryProvider {
            featureAEntryBuilder(
                backStack = backStack,
                registerViewModel = registerViewModel,
                welcomeViewModel = welcomeViewModel,
            )
        },
        transitionSpec = {
            // Slide new content horizontal, keeping the old content in place underneath
            slideInHorizontally (
                initialOffsetX = { it },
                animationSpec = tween(500)
            ) togetherWith ExitTransition.None
        },
        popTransitionSpec = {
            // Slide old content horizontal, revealing the new content in place underneath
            EnterTransition.None togetherWith
                    slideOutHorizontally (
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
        },
        predictivePopTransitionSpec = {
            // Slide old content horizontal, revealing the new content in place underneath
            EnterTransition.None togetherWith
                    slideOutHorizontally (
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
        },
    )
}