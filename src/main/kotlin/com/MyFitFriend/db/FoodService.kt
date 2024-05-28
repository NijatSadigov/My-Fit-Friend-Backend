package com.MyFitFriend.db

import com.MyFitFriend.data.model.Food


interface FoodService {
    suspend fun createFood( food: Food):Food?
    suspend fun editFood(foodId:Int, food: Food):Boolean
    suspend fun removeFood( foodId: Int):Boolean
    suspend fun getFoodIdByFoodItem(foodItem:String):Int
    suspend fun getFood(foodId:Int):Food?
    suspend fun getFoodIdByQR(qrCode:String):Int?
    suspend fun getAllFoods():List<Food>

}