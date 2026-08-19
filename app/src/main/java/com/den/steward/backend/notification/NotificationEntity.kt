package com.den.steward.backend.notification

data class NotificationEntity (
    val title: String,
    val message: String,
    val bigMessage: String? = null,
    val icon: Int,
    val largeIcon: Int
)