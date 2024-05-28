package com.MyFitFriend.requests

import kotlinx.serialization.Serializable

@Serializable
data class DietaryLogRequest(

//    val date: String,
    val partOfDay:Int, //1morning 2 lunch 3 dinner 4 breakfast
    //val foodItem: String,
    val amountOfFood:Double,
    val foodId:Int
//    val calories: Int,
//    val carbs: Double,
//    val protein: Double,
//    val fats: Double,
  //  val dietaryLogId: Int = 0
)
