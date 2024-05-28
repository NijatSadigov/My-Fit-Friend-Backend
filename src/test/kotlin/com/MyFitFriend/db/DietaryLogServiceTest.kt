package com.MyFitFriend.db

import org.junit.jupiter.api.TestInstance
import org.koin.test.KoinTest

import com.MyFitFriend.di.appModule
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.inject
import com.MyFitFriend.data.model.DietaryLog
import com.MyFitFriend.data.model.DietaryLogs
import com.MyFitFriend.data.model.User
import com.MyFitFriend.data.model.Users
import com.MyFitFriend.userService


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DietaryLogServiceTest : KoinTest {

    private val dietaryLogService: DietaryLogService by inject()
    private val userService: UserService by inject()

    @BeforeAll
    fun beforeAll() {
        startKoin {
            modules(appModule)
        }
    }

    @BeforeEach
    fun setup() {
        // Initialize in-memory database
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            // Create tables
            SchemaUtils.create(DietaryLogs)
            SchemaUtils.create(Users)
        }

    }

    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(DietaryLogs)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test addDietaryLog`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        val dietaryLog = DietaryLog(
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        )
        val result = dietaryLogService.addDietaryLog(dietaryLog)
        assertNotNull(result)
    }

    @Test
    fun `test removeDietaryLog`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )

        val dietaryLog = DietaryLog(
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        )
         dietaryLogService.addDietaryLog(dietaryLog)

        val result = dietaryLogService.removeDietaryLog(1)
        assertTrue(result)
    }

    @Test
    fun `test editDietaryLog`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        val dietaryLog = DietaryLog(
            dietaryLogId = 1,
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        )
        dietaryLogService.addDietaryLog(dietaryLog)

        val updatedDietaryLog = dietaryLog.copy(foodItem = "Banana")
        val result = dietaryLogService.editDietaryLog(1, updatedDietaryLog)
        assertTrue(result)
    }

    @Test
    fun `test getDietaryLogs`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        val logs = dietaryLogService.getDietaryLogs()
        assertNotNull(logs)
    }

    @Test
    fun `test getDietaryLog`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        dietaryLogService.addDietaryLog(DietaryLog(
            dietaryLogId = 1,
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        ))

        val dietaryLog = dietaryLogService.getDietaryLog(1)
        assertNotNull(dietaryLog)
    }

    @Test
    fun `test getDietaryLogByDateAndPartOfDay`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        val dietaryLog = dietaryLogService.getDietaryLogByDateAndPartOfDay(1, "2023-05-28", 1)
        assertNotNull(dietaryLog)
    }

    @Test
    fun `test getDietaryLogsByUserId`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        val dietaryLogs = dietaryLogService.getDietaryLogsByUserId(1)
        assertNotNull(dietaryLogs)
    }

    @Test
    fun `test isOwnedByUser`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        dietaryLogService.addDietaryLog(DietaryLog(
            dietaryLogId = 1,
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        ))
        val result = dietaryLogService.isOwnedByUser(1, 1)
        assertTrue(result)
    }

    @Test
    fun `test getDietaryLogByDate`() = runBlocking {
        userService.addUser( User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)
        )
        dietaryLogService.addDietaryLog(DietaryLog(
            dietaryLogId = 1,
            userId = 1,
            date = "2023-05-28",
            foodItem = "Apple",
            partOfDay = 1,
            amountOfFood = 1.0,
            foodId = 1,
            lastEditDate = System.currentTimeMillis()
        ))
        val dietaryLogs = dietaryLogService.getDietaryLogByDate(1, "2023-05-28")
        assertNotNull(dietaryLogs)
    }
}

