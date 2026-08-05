package com.den.steward.authScreenTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.den.steward.BaseTest
import com.den.steward.R
import com.den.steward.ui.screens.authScreen.registerScreen.PasswordScreen
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestPasswordScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            PasswordScreen(
                backStack = backStack,
                registerViewModel = hiltViewModel()
            )
        }
    }

    @Test
    fun isPasswordTitleDisplay() {
        val text = composeRule.activity.getString(R.string.password_screen_title)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isPasswordDescriptionDisplay() {
        val text = composeRule.activity.getString(R.string.password_screen_description)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isPasswordFieldDisplay() {
        val tag = composeRule.activity.getString(R.string.password_screen_password_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isConfirmPasswordFieldDisplay() {
        val tag = composeRule.activity.getString(R.string.password_screen_confirm_password_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isRegisterButtonDisplay() {
        val tag = composeRule.activity.getString(R.string.password_screen_register_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isFooterDisplay() {
        val tag = composeRule.activity.getString(R.string.footer)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}