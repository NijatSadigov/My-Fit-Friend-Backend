package com.MyFitFriend.db
import com.MyFitFriend.data.model.*
import com.MyFitFriend.di.appModule
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkoutServiceTest : KoinTest {

    private val workoutService: WorkoutService by inject()

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
            SchemaUtils.create(Users, Workouts,Exercises)

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
        }
    }

    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(Workouts, Users,Exercises)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test createWorkout`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        val result = workoutService.createWorkout(workout)
        assertNotNull(result)
    }

    @Test
    fun `test removeWorkout`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)
        val result = workoutService.removeWorkout(0)
        assertTrue(result)
    }

    @Test
    fun `test editWorkout`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)

        val updatedWorkout = workout.copy(description = "Evening Run")
        val result = workoutService.editWorkout(0, updatedWorkout)
        assertNotNull(result)
    }

    @Test
    fun `test getWorkouts`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)

        val workouts = workoutService.getWorkouts()
        assertNotNull(workouts)
        assertTrue(workouts.isNotEmpty())
    }

    @Test
    fun `test getWorkout`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)

        val result = workoutService.getWorkout(0)
        assertNotNull(result)
    }

    @Test
    fun `test getAllWorkoutsByUserId`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)

        val workouts = workoutService.getAllWorkoutsByUserId(1)
        assertNotNull(workouts)
        assertTrue(workouts.isNotEmpty())
    }

    @Test
    fun `test isOwnedByUser`() = runBlocking {
        val workout = Workout(
            userId = 1,
            description = "Morning Run",
            date = "2023-05-28",
            title = "Run",
            lastEditDate = System.currentTimeMillis()
        )
        workoutService.createWorkout(workout)

        val result = workoutService.isOwnedByUser(1, 0)
        assertTrue(result)
    }
}