package com.MyFitFriend.data.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Food (
    val foodId:Int,
    val foodName:String,
    val cal:Double,
    val protein:Double,
    val carb:Double,
    val fat:Double,
    val qrCode: String? = null  // Optional field, default is null



)
object Foods:Table(){
    val foodId=integer("foodId").autoIncrement()
    val foodName=varchar("foodName",255)
    val cal=double("cal")
    val protein=double("protein")
    val carb=double("carb")
    val fat=double("fat")
    val qrCode = varchar("qrCode", 255).nullable()  // Nullable varchar column



    override val primaryKey =PrimaryKey(foodId)
}