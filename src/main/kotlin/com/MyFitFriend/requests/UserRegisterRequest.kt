package com.MyFitFriend.requests

data class UserRegisterRequest (
    val username: String,
    val passwordHash: String,
    val email: String,
    val weight:Double,
    val height:Double,
    val activityLevel:Int,
    val age:Int,
    val sex:Boolean
    )