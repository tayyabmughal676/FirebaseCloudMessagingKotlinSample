package com.gaalbaat.fcmsample.notification.model

data class PushNotification(
    val data: NotificationData,
    val to: String
)