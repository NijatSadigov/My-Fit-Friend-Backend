package com.MyFitFriend.plugins

import com.MyFitFriend.data.model.*
import com.MyFitFriend.data.model.DietGroupMembers
import com.MyFitFriend.data.model.DietGroups

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val driverClass = environment.config.property("storage.driverClassName").getString()
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()

    val db = Database.connect(provideDataSource(jdbcUrl, driverClass))

    try {
        transaction(db) {
            SchemaUtils.create(Users, Exercises, DietaryLogs, Workouts, Foods, DietGroups, DietGroupMembers,DietGroupRequests)
        }
        log.info("Database schema created/verified successfully.")
    } catch (e: Exception) {
        log.error("Error during database schema creation", e)
        throw IllegalStateException("Failed to create/verify database schema", e)
    }
}

private fun provideDataSource(url: String, driverClass: String): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = driverClass
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}
suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}