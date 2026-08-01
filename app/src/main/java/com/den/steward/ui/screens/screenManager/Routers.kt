// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.screenManager

import androidx.navigation.NavType
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRouter: NavKey


@Serializable
data object HomeRouter: NavKey

@Serializable
data object LoginRouter: NavKey

@Serializable
data object EmailRouter: NavKey

@Serializable
data object NameRouter: NavKey

@Serializable
data object PasswordRouter: NavKey


@Serializable
data object ForgotPasswordRouter: NavKey

@Serializable
data object SettingsRouter: NavKey
