package com.MyFitFriend.db

import org.junit.jupiter.api.TestInstance
import org.koin.test.KoinTest
import com.MyFitFriend.data.model.User
import com.MyFitFriend.data.model.Users
import com.MyFitFriend.di.appModule
import com.MyFitFriend.requests.userEditRequest
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest: KoinTest
{
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
            // Initialize tables if necessary
            SchemaUtils.create(Users)

        }
    }
    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(Users)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test addUser`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

   //     ("1", user"John Doe", "john@example.com", "hashedpassword", 70.0, 175.0, "Active", 30, "Male", System.currentTimeMillis())
        val result = userService.addUser(user)
        assertTrue(result)
    }

    @Test
    fun `test removeUser`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val result = userService.removeUser(1)
        assertTrue(result)
    }

    @Test
    fun `test editUser`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val userEdit = userEditRequest("John Doe", 75.0, 180.0, 2, 31, false, System.currentTimeMillis())


        val result = userService.editUser(1, userEdit)
        assertTrue(result)
    }

    @Test
    fun `test getUsers`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val users = userService.getUsers()
        assertNotNull(users)
        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `test getUser`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val resultUser = userService.getUser(1)
        assertNotNull(resultUser)
    }

    @Test
    fun `test getUserIdByEmail`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val userId = userService.getUserIdByEmail("john@example.com")
        assertNotNull(userId)
    }



    @Test
    fun `test emailAlreadyUsed`() = runBlocking {
        val user = User(username = "John Doe", passwordHash = "hasshedPassword", activityLevel = 1, age = 19,
            email = "john@example.com", height = 70.0, lastEditDate = System.currentTimeMillis(), sex = true, weight = 43.4)

        userService.addUser(user)
        val result = userService.emailAlreadyUsed("john@example.com")
        assertTrue(result)
    }




}