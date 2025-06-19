package com.example.academically.data.model

data class LocalStudentSubscription(
    val id: Int,
    val studentId: Int = 1,
    val channelId: Int,
    val channelName: String,
    val organizationName: String,
    val subscribedAt: String,
    val isActive: Boolean = true,
    val notificationsEnabled: Boolean = true
) {

}
