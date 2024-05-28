package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable

import org.jetbrains.exposed.sql.Table

@Serializable
data class UserEntity(
    val username: String,
    val passwordHash: String,
    val email: String,
    val weight:Double,
    val height:Double,
    val activityLevel:Int,
    val userId: Int = 0, // Assuming an auto-increment ID is used
    val age:Int,
    val sex:Boolean,
    val lastEditDate:Long,
    val isSync:Boolean=true
)
