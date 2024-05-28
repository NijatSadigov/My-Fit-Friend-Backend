package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseEntity(
    val lastEditDate:Long,
    val workoutId:Int,
    val description:String,
    val title:String,
    val weights:Double,
    val setCount:Int,
    val repCount:Int,
    val restTime:Double,
    val isSync:Boolean=true,
    val isAdded:Boolean=true,
    val exerciseId: Int
)
