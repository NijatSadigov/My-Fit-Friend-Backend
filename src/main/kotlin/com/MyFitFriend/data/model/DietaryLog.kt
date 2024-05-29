package com.MyFitFriend.data.model

import com.MyFitFriend.data.model.Users
import com.MyFitFriend.data.model.Workouts.autoIncrement
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Serializable
data class DietaryLog(
    val userId: Int,
    val date: String,
    val partOfDay:Int,
    val foodItem: String,
    val foodId:Int,
    val amountOfFood:Double,
    val lastEditDate:Long,

    val dietaryLogId: Int = 0
)

object DietaryLogs : IntIdTable() {
    val userId = integer("userId").references(Users.userId)
    val date = varchar("date", 255)
    val partOfDay=integer("partOfDay")
    val amountOfFood=double("amountOfFood")
    val foodId = integer("foodId")
    val foodItem = varchar("foodItem", 255)
    val lastEditDate=long("lastEditDate")

}