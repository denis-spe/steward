package com.den.steward.authScreenTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.den.steward.BaseTest
import com.den.steward.R
import com.den.steward.ui.screens.authScreen.registerScreen.EmailScreen
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestEmailScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            EmailScreen(
                backStack = backStack,
                registerViewModel = hiltViewModel()
            )
        }
    }

    @Test
    fun isEmailTitleDisplay() {
        val text = composeRule.activity.getString(R.string.email_screen_title)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isEmailDescriptionDisplay() {
        val text = composeRule.activity.getString(R.string.email_screen_description)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isEmailFieldDisplay() {
        val tag = composeRule.activity.getString(R.string.email_screen_email_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isNextButtonDisplay() {
        val tag = composeRule.activity.getString(R.string.email_screen_next_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isFooterDisplay() {
        val tag = composeRule.activity.getString(R.string.footer)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}