package com.MyFitFriend.data.model

import com.MyFitFriend.data.model.Workouts.autoIncrement
import com.MyFitFriend.data.model.Workouts.references
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Exercise(
    val workoutId:Int,
    val exerciseId: Int = 0,
    val title:String,
    val description: String="",
    val weights:Double=0.0,
    val setCount:Int=0,
    val repCount:Int=0,
    val restTime:Double=0.0,
    val lastEditDate:Long
)

object Exercises : IntIdTable() {
    val workoutId = integer("workoutId").references(Workouts.id)
    val description = text("description")
    val weights=double("weights")
    val setCount=integer("setCount")
    val repCount=integer("repCount")
    val restTime=double("restTime")
    val title=varchar("title",255)
    val lastEditDate=long ("lastEditDate")


}
