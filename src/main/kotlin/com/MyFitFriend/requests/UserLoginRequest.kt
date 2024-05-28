package com.MyFitFriend.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRequest(
    val passwordHash: String,
    val email: String,
)
