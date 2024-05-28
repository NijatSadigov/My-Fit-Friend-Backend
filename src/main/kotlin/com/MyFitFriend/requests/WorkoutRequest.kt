package com.MyFitFriend.requests

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRequest (
    val title:String,
    val description:String,
    val lastEditDate:Long,
    val date:String,
    val userId:Int
)