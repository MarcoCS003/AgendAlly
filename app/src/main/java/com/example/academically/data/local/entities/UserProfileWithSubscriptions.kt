package com.example.academically.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.UserProfileEntity

data class UserProfileWithSubscriptions(
    @Embedded val profile: UserProfileEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val subscriptions: List<StudentSubscriptionEntity>
)