package com.MyFitFriend.db

import com.MyFitFriend.data.model.*
import com.MyFitFriend.di.appModule
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExerciseServiceTest : KoinTest {

    private val exerciseService: ExerciseService by inject()

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
            // Create tables in the correct order
            SchemaUtils.create(Users, Workouts, Exercises)

            // Insert a test user
            Users.insert {
                it[username] = "Test User"
                it[email] = "test@example.com"
                it[passwordHash] = "hashedPassword"
                it[weight] = 70.0
                it[height] = 175.0
                it[activityLevel] = 1
                it[age] = 30
                it[sex] = true
                it[lastEditDate] = System.currentTimeMillis()
            }

            // Insert a test workout
            Workouts.insert {
                it[userId] = 1
                it[date] = "2024-05-28"
                it[title] = "Test Workout"
                it[description] = "This is a test workout"
                it[lastEditDate] = System.currentTimeMillis()
            }
        }
    }

    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(Exercises, Workouts, Users)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test addExercise`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        val addedExercise = exerciseService.addExercise(exercise)
        assertNotNull(addedExercise)
        assertEquals("Test Exercise", addedExercise?.title)
    }

    @Test
    fun `test removeExercise`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        val addedExercise = exerciseService.addExercise(exercise)
        val result = exerciseService.removeExercise(addedExercise!!.exerciseId)
        assertTrue(result)
    }

    @Test
    fun `test editExercise`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        val addedExercise = exerciseService.addExercise(exercise)
        val updatedExercise = addedExercise!!.copy(title = "Updated Test Exercise")
        val result = exerciseService.editExercise(addedExercise.exerciseId, updatedExercise)
        assertTrue(result)
        val fetchedExercise = exerciseService.getExercise(addedExercise.exerciseId)
        assertEquals("Updated Test Exercise", fetchedExercise?.title)
    }

    @Test
    fun `test getExercises`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        exerciseService.addExercise(exercise)
        val exercises = exerciseService.getExercises()
        assertNotNull(exercises)
        assertTrue(exercises.isNotEmpty())
    }

    @Test
    fun `test getExercise`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        val addedExercise = exerciseService.addExercise(exercise)
        val fetchedExercise = exerciseService.getExercise(addedExercise!!.exerciseId)
        assertNotNull(fetchedExercise)
        assertEquals("Test Exercise", fetchedExercise?.title)
    }

    @Test
    fun `test getExercisesByWorkoutId`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        exerciseService.addExercise(exercise)
        val exercises = exerciseService.getExercisesByWorkoutId(1)
        assertNotNull(exercises)
        assertTrue(exercises.isNotEmpty())
    }

    @Test
    fun `test removeExercisesByWorkoutId`() = runBlocking {
        val exercise = Exercise(
            workoutId = 1,
            title = "Test Exercise",
            description = "This is a test exercise",
            weights = 50.0,
            setCount = 3,
            repCount = 10,
            restTime = 60.0,
            lastEditDate = System.currentTimeMillis()
        )
        exerciseService.addExercise(exercise)
        val result = exerciseService.removeExercisesByWorkoutId(1)
        assertTrue(result)
    }
}
