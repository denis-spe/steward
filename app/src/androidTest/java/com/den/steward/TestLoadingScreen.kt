package com.den.steward

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.den.steward.ui.screens.loadingScreen.LoadingScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestLoadingScreen : BaseTest() {
    @Before
    fun init() {
        hiltRule.inject()

        composeRule.setContent {
            LoadingScreen()
        }
    }

    @Test
    fun isLoadingIndicatorDisplay() {
        val tag = composeRule.activity.getString(R.string.loading_indicator)
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }
}