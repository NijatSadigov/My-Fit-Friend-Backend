package com.MyFitFriend.db

import com.MyFitFriend.data.model.DietaryLog
import com.MyFitFriend.data.model.DietaryLogs
import com.MyFitFriend.plugins.dbQuery
import org.jetbrains.exposed.sql.*

class DietaryLogServiceIMPL:DietaryLogService {
    private fun resultRowToDietaryLog(row: ResultRow): DietaryLog {
        return DietaryLog(
            dietaryLogId = row[DietaryLogs.id].value,
            userId=row[DietaryLogs.userId],
            date=row[DietaryLogs.date],
            foodItem = row[DietaryLogs.foodItem],
            partOfDay = row[DietaryLogs.partOfDay],
            amountOfFood = row[DietaryLogs.amountOfFood],
            foodId=row[DietaryLogs.foodId],
            lastEditDate = row[DietaryLogs.lastEditDate]
        )
    }
    override suspend fun addDietaryLog(dietaryLog: DietaryLog): DietaryLog? = dbQuery{
        try {

            val insertedId = dbQuery {
                DietaryLogs.insertAndGetId {
                    it[userId] = dietaryLog.userId
                    it[date] = dietaryLog.date
                    it[foodItem] = dietaryLog.foodItem
                    it[partOfDay] = dietaryLog.partOfDay
                    it[foodId] = dietaryLog.foodId
                    it[amountOfFood] = dietaryLog.amountOfFood
                    it[lastEditDate] = dietaryLog.lastEditDate
                }.value
            }

            // Retrieve the newly inserted dietary log by the insertedId
            return@dbQuery DietaryLogs.select { DietaryLogs.id eq insertedId }
                .mapNotNull { resultRowToDietaryLog(it) }
                .singleOrNull()


        } catch (e: Exception) {
            // Handle potential errors such as connection issues, constraint violations, etc.
            e.printStackTrace()
            null
        }

    }

    override suspend fun removeDietaryLog(dietaryLogId: Int): Boolean = dbQuery {
        DietaryLogs.deleteWhere { DietaryLogs.id eq dietaryLogId } >0
    }

    override suspend fun editDietaryLog(dietaryLogId: Int, dietaryLog: DietaryLog): Boolean = dbQuery {
        DietaryLogs.update ({ DietaryLogs.id eq dietaryLogId }){
            it[date]=dietaryLog.date
            it[foodItem]=dietaryLog.foodItem
            it[partOfDay]=dietaryLog.partOfDay
            it[amountOfFood]=dietaryLog.amountOfFood
            it[foodId]=dietaryLog.foodId
            it[lastEditDate]=dietaryLog.lastEditDate


        }>0
    }

    override suspend fun getDietaryLogs(): List<DietaryLog> = dbQuery{
        DietaryLogs.selectAll().map { resultRowToDietaryLog(it) }

    }

    override suspend fun getDietaryLogByDateAndPartOfDay(userId: Int,date: String, partOfDay: Int): List<DietaryLog> = dbQuery{
        DietaryLogs.select{
            (DietaryLogs.userId eq userId) and (DietaryLogs.date eq date) and (DietaryLogs.partOfDay eq partOfDay)

        }.map{resultRowToDietaryLog(it)}
    }

    override suspend fun getDietaryLog(id: Int): DietaryLog? = dbQuery {
        DietaryLogs.select { (DietaryLogs.id eq id) }.map { resultRowToDietaryLog(it) }.singleOrNull()
    }

    override suspend fun getDietaryLogsByUserId(userId: Int): List<DietaryLog> = dbQuery{
        DietaryLogs.select { (DietaryLogs.userId eq userId) }.map{resultRowToDietaryLog(it)  }
    }

    override suspend fun isOwnedByUser(userId: Int, dietaryLogId: Int): Boolean = dbQuery{
        val dietaryLog = DietaryLogs.select { DietaryLogs.id eq dietaryLogId }
            .mapNotNull { resultRowToDietaryLog(it) }
            .singleOrNull()
        dietaryLog!=null && dietaryLog.userId == userId
    }

    override suspend fun getDietaryLogByDate(userId: Int, date: String): List<DietaryLog> = dbQuery {
        DietaryLogs.select { (DietaryLogs.userId eq userId) and (DietaryLogs.date eq date) }.map{resultRowToDietaryLog(it)  }

    }
}