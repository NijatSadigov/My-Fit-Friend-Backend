package com.MyFitFriend.requests

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseRequest(
    val description: String="",
    val weights:Double=0.0,
    val setCount:Int=0,
    val repCount:Int=0,
    val restTime:Double=0.0,
    val title:String,
    val lastEditDate:Long
)
