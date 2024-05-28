package com.MyFitFriend.requests

import kotlinx.serialization.Serializable

@Serializable
data class userEditRequest(

    val username: String,
    val weight:Double,
    val height:Double,
    val activityLevel:Int,
    val age:Int,
    val sex:Boolean,
    val lastEditDate:Long
)