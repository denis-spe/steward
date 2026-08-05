package com.den.steward

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.den.steward.ui.screens.settingsScreen.SettingsScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestSettingsScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            val backStack = rememberNavBackStack()
            SettingsScreen(
                backStack = backStack,
                settingsViewModel = hiltViewModel()
            )
        }
    }

    @Test
    fun isLogoutButtonDisplay() {
        val tag = composeRule.activity.getString(R.string.settings_screen_logout_button)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}