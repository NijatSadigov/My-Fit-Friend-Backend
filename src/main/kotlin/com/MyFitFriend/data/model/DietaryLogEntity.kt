package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DietaryLogEntity(

    val lastEditDate:Long,
    val date: String,
    val partOfDay:Int, //0morning 1 lunch 2 dinner 3 breakfast
    val foodItem: String,
    val foodId:Int,
    val amountOfFood:Double,
    val userId:Int,
//    val calories: Int,
//    val carbs: Double,
//    val protein: Double,
//    val fats: Double,
    var isSync:Boolean=true,
    var isAdded:Boolean=true,
    val dietaryLogId: Int=0
)
