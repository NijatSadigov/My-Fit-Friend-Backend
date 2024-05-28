package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable

import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val username: String,
    val passwordHash: String,
    val email: String,
    val weight:Double,
    val height:Double,
    val activityLevel:Int,
    val userId: Int = 0, // Assuming an auto-increment ID is used
    val age:Int,
    val sex:Boolean,
    val lastEditDate:Long
)

object Users : Table() {
    val userId = integer("userId").autoIncrement()
    val username = varchar("username", 255)
    val passwordHash = varchar("passwordHash", 255)
    val email = varchar("email", 255)
    val weight = double("weight")
    val height = double("height")
    val activityLevel = integer("activityLevel")
    val sex=bool("sex")
    val lastEditDate=long("lastEditDate")
    val age=integer("age")
    override val primaryKey = PrimaryKey(userId, name = "PK_User_ID") // Naming the primary key is optional
}