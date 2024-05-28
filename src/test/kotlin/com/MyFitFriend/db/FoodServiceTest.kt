package com.MyFitFriend.db

import com.MyFitFriend.data.model.Food
import com.MyFitFriend.data.model.Foods
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
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FoodServiceTest : KoinTest {

    private val foodService: FoodService by inject()

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
            SchemaUtils.create(Foods)
        }
    }

    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(Foods)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test createFood`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        val result = foodService.createFood(food)
        assertNotNull(result)
    }

    @Test
    fun `test editFood`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val updatedFood = food.copy(foodName = "Banana")
        val result = foodService.editFood(1, updatedFood)
        assertTrue(result)
    }

    @Test
    fun `test removeFood`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val result = foodService.removeFood(1)
        assertTrue(result)
    }

    @Test
    fun `test getFoodIdByFoodItem`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val foodId = foodService.getFoodIdByFoodItem("Apple")
        assertTrue(foodId != -1)
    }

    @Test
    fun `test getFood`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val result = foodService.getFood(1)
        assertNotNull(result)
    }

    @Test
    fun `test getFoodIdByQR`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val foodId = foodService.getFoodIdByQR("12345")
        assertNotNull(foodId)
    }

    @Test
    fun `test getAllFoods`() = runBlocking {
        val food = Food(
            foodId = 1,
            foodName = "Apple",
            cal = 52.0,
            protein = 0.3,
            carb = 14.0,
            fat = 0.2,
            qrCode = "12345"
        )
        foodService.createFood(food)

        val foods = foodService.getAllFoods()
        assertNotNull(foods)
        assertTrue(foods.isNotEmpty())
    }
}
