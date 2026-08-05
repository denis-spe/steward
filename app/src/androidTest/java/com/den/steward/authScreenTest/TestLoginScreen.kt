// Glory be to LORD GOD
package com.den.steward.authScreenTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.den.steward.BaseTest
import com.den.steward.ui.screens.authScreen.loginScreen.LoginScreen
import com.den.steward.ui.screens.authScreen.registerScreen.NameScreen
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestLoginScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            LoginScreen(
                backStack = backStack,
                loginViewModel = hiltViewModel()
            )
        }
    }

    @Test
    fun isLoginTitleDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.login_screen_title)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isLoginDescriptionDisplay() {
        val text = composeRule.activity.getString(com.den.steward.R.string.login_description)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isEmailFieldDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.login_email_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isPasswordFieldDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.login_password_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isForgotPasswordDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.login_forgot_password)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isLoginButtonDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.login_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isFooterDisplay() {
        val tag = composeRule.activity.getString(com.den.steward.R.string.footer)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}