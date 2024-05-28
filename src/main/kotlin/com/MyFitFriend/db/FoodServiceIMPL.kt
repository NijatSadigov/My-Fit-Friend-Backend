package com.MyFitFriend.db

import com.MyFitFriend.data.model.*
import com.MyFitFriend.plugins.dbQuery
import org.jetbrains.exposed.sql.*

class FoodServiceIMPL:FoodService {
    private fun resultRowToFood(row: ResultRow): Food {
        return Food(
            foodName=row[Foods.foodName],
            cal=row[Foods.cal],
            fat = row[Foods.fat],
            protein = row[Foods.protein],
            carb = row[Foods.carb],
            foodId=row[Foods.foodId],
            qrCode = row[Foods.qrCode]
        )
    }
    override suspend fun createFood(food: Food): Food? = dbQuery {
        val insertStmt=Foods.insert {
            it[foodId]=food.foodId
            it[cal]=food.cal
            it[foodName]=food.foodName
            it[protein]=food.protein
            it[carb]=food.carb
            it[fat]=food.fat
            it[qrCode]=food.qrCode
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToFood(it) }
    }

    override suspend fun editFood(foodId:Int,food: Food): Boolean = dbQuery{
        Foods.update ({ Foods.foodId eq foodId }){
            it[foodName]=food.foodName
            it[cal]=food.cal
            it[protein]=food.protein
            it[carb]=food.carb
            it[fat]=food.fat
            it[qrCode]=food.qrCode

        }>0


    }

    override suspend fun removeFood(foodId: Int): Boolean = dbQuery{
        Foods.deleteWhere { Foods.foodId eq foodId } >0
    }

    override suspend fun getFoodIdByFoodItem(foodItem:String): Int = dbQuery {
        Foods.select { Foods.foodName eq foodItem }
            .map { resultRowToFood(it).foodId }
            .singleOrNull()
            ?: -1
    }


    override suspend fun getFood(foodId: Int): Food?= dbQuery {

            Foods.select { (Foods.foodId eq foodId) }.map{resultRowToFood(it)  }.singleOrNull()

    }

    override suspend fun getFoodIdByQR(qrCode: String): Int? = dbQuery{
        val food = Foods.select { (Foods.qrCode eq qrCode) }.map{resultRowToFood(it)  }.singleOrNull()
        food?.foodId
    }
    override suspend fun getAllFoods():List<Food> = dbQuery {
        Foods.selectAll().map { resultRowToFood(it) }
    }






    }

