package com.example.myfitfriend.data.remote.reponses

import kotlinx.serialization.Serializable

@Serializable
data class GroupDietaryLogsItem(
    val userName:String, //
    val userId:Int,//
    val maxCalories:Double,//
    val totalCalories:Double,
    val totalProtein:Double,
    val totalCarb:Double,
    val totalFat:Double



)
