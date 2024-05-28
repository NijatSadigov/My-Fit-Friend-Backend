package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

import org.jetbrains.exposed.sql.Table

@Serializable
data class Workout(
    val userId: Int,
    val description:String,
    val date: String, // Using String for simplicity, convert to date as needed
    val workoutId: Int=0,
    val title:String,
    val lastEditDate:Long

)


object Workouts : IntIdTable() {
    val userId = integer("userId").references(Users.userId)
    val description = text("description")
    val date= varchar("date", 255)
    val title=varchar("title",255)
    val lastEditDate=long("lastEditDate")

}