package com.den.steward

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.den.steward.ui.screens.welcomeScreen.WelcomeScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestWelcomeScreen: BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            WelcomeScreen(
                backStack = backStack,
                welcomeViewModel = hiltViewModel()
            )
        }
    }

    // Compose UI tests here.
    @Test
    fun mainTitle() {
        composeRule.onNodeWithText("Steward").assertIsDisplayed()
    }

    @Test
    fun mainDescription() {
        val tag = composeRule.activity.getString(R.string.welcome_description)
        composeRule.onNodeWithText(tag).assertIsDisplayed()
    }

    @Test
    fun loginButtonIsDisplayed() {
        val tag = composeRule.activity.getString(R.string.login_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun registerButtonIsDisplayed() {
        val tag = composeRule.activity.getString(R.string.register_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun googleButtonIsDisplayed() {
        val tag = composeRule.activity.getString(R.string.google_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun anonymousButtonIsDisplayed() {
        val tag = composeRule.activity.getString(R.string.anonymous_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun footerIsDisplayed() {
        val tag = composeRule.activity.getString(R.string.footer)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

}