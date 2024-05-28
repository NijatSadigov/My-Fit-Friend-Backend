package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutEntity(
    val lastEditDate:Long,
    val description:String,
    val date:String,
    val userId:Int,
//    val calories: Int,
//    val carbs: Double,9
//    val protein: Double,
//    val fats: Double,
    val isSync:Boolean=true,
    val title:String,
val isAdded:Boolean=true,
    val workoutId: Int=0
)