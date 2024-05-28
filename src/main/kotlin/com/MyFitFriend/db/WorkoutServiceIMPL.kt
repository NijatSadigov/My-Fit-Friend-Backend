package com.MyFitFriend.db

import com.MyFitFriend.data.model.*
import com.MyFitFriend.exerciseService
import com.MyFitFriend.plugins.dbQuery
import org.jetbrains.exposed.sql.*

class WorkoutServiceIMPL:WorkoutService {
    private fun resultRowToWorkoutService(row: ResultRow): Workout {
        return Workout(
            workoutId = row[Workouts.id].value,
            userId=row[Workouts.userId],
            date=row[Workouts.date],
            title=row[Workouts.title],
            description=row[Workouts.description],
            lastEditDate = row[Workouts.lastEditDate]

        )
    }


    override suspend fun createWorkout(workout: Workout): Workout? = dbQuery {
val insertId=Workouts.insertAndGetId {
    it[id]=workout.workoutId
    it[userId]=workout.userId
    it[date]=workout.date
    it[title]=workout.title
    it[description]=workout.description
    it[lastEditDate]=workout.lastEditDate

}
        return@dbQuery Workouts.select { Workouts.id eq insertId }
            .mapNotNull { resultRowToWorkoutService(it) }
            .singleOrNull()
    }

    override suspend fun removeWorkout(workoutId: Int): Boolean= dbQuery {
        exerciseService.removeExercisesByWorkoutId(workoutId)
        Workouts.deleteWhere { Workouts.id eq workoutId } >0
    }

    override suspend fun editWorkout(workoutId: Int, workout: Workout): Workout? = dbQuery {
        Workouts.update({ Workouts.id eq workoutId }) {
            it[userId] = workout.userId
            it[date] = workout.date
            it[title] = workout.title
            it[description] = workout.description
            it[lastEditDate]=workout.lastEditDate
        }>0
        return@dbQuery Workouts.select { Workouts.id eq workoutId }
            .mapNotNull { resultRowToWorkoutService(it) }
            .singleOrNull()
    }
    override suspend fun getWorkouts(): List<Workout> = dbQuery {
        Workouts.selectAll().map { resultRowToWorkoutService(it) }
    }

    override suspend fun getWorkout(id: Int): Workout? = dbQuery {
        Workouts.select { (Workouts.id eq id) }.map { resultRowToWorkoutService(it) }.singleOrNull()
    }

    override suspend fun getAllWorkoutsByUserId(userId: Int): List<Workout> = dbQuery{
        Workouts.select { (Workouts.userId eq userId) }.map{resultRowToWorkoutService(it)  }
    }

    override suspend fun isOwnedByUser(userId: Int, workoutId: Int): Boolean = dbQuery {
        val workout = Workouts.select { Workouts.id eq workoutId }
            .mapNotNull { resultRowToWorkoutService(it) }
            .singleOrNull()
        workout!=null && workout.userId == userId    }


}