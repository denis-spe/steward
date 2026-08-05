// Glory be to LORD GOD of host and JESUS CHRIST
package com.den.steward.authScreenTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.den.steward.BaseTest
import com.den.steward.R
import com.den.steward.ui.screens.authScreen.registerScreen.NameScreen
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestNameScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            NameScreen(
                backStack = backStack,
                registerViewModel = hiltViewModel()
            )
        }
    }


    @Test
    fun isNameTitleDisplay() {
        val text = composeRule.activity.getString(R.string.name_screen_title)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isNameDescriptionDisplay() {
        val text = composeRule.activity.getString(R.string.name_screen_description)
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun isFirstNameFieldDisplay() {
        val tag = composeRule.activity.getString(R.string.name_screen_first_name_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isLastNameFieldDisplay() {
        val tag = composeRule.activity.getString(R.string.name_screen_last_name_field)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun isFooterDisplay() {
        val tag = composeRule.activity.getString(R.string.footer)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}