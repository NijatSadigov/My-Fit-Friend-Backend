package com.MyFitFriend.db

import com.MyFitFriend.data.model.DietaryLog


interface DietaryLogService {
    suspend fun addDietaryLog(dietaryLog: DietaryLog):DietaryLog?
    suspend fun removeDietaryLog(dietaryLogId :Int):Boolean
    suspend fun editDietaryLog(dietaryLogId :Int , dietaryLog: DietaryLog):Boolean
    suspend fun getDietaryLogs():List<DietaryLog>
    suspend fun getDietaryLogByDateAndPartOfDay(userId:Int,date: String, partOfDay:Int):List<DietaryLog>
    //suspend fun searchWorkout(query:String):List<User>
    suspend fun getDietaryLog(id:Int): DietaryLog?
    suspend fun getDietaryLogsByUserId(userId:Int): List<DietaryLog>
    suspend fun isOwnedByUser(userId: Int,dietaryLogId: Int):Boolean
    suspend fun getDietaryLogByDate(userId: Int,date:String):List<DietaryLog>
}