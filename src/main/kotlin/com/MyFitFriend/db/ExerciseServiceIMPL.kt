package com.MyFitFriend.db

import com.MyFitFriend.data.model.*
import com.MyFitFriend.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ExerciseServiceIMPL:ExerciseService {
    private fun resultRowToExercise(row: ResultRow): Exercise {
        return Exercise(
            workoutId = row[Exercises.workoutId],
            exerciseId=row[Exercises.id].value,
            weights=row[Exercises.weights],
            setCount=row[Exercises.setCount],
            repCount=row[Exercises.repCount],
            restTime=row[Exercises.restTime],
            description=row[Exercises.description],
            title=row[Exercises.title],
            lastEditDate = row[Exercises.lastEditDate]

        )
    }
    override suspend fun addExercise(exercise: Exercise): Exercise?= dbQuery {
        val insertedId=Exercises.insertAndGetId {
            it[workoutId]=exercise.workoutId
            it[weights]=exercise.weights
            it[setCount]=exercise.setCount
            it[repCount]=exercise.repCount
            it[restTime]=exercise.restTime
            it[description]=exercise.description
            it[title]=exercise.title
            it[lastEditDate]=exercise.lastEditDate


        }
        return@dbQuery Exercises.select { Exercises.id eq insertedId }
            .mapNotNull { resultRowToExercise(it) }
            .singleOrNull()

    }

    override suspend fun removeExercise(exerciseId: Int): Boolean= dbQuery {
        Exercises.deleteWhere { Exercises.id eq exerciseId } >0
    }

    override suspend fun editExercise(exerciseId: Int, exercise: Exercise): Boolean= dbQuery {
        Exercises.update  ({ Exercises.id eq exerciseId }) {
            it[workoutId]=exercise.workoutId
            it[weights]=exercise.weights
            it[setCount]=exercise.setCount
            it[repCount]=exercise.repCount
            it[restTime]=exercise.restTime
            it[description]=exercise.description
            it[title]=exercise.title


        }    } >0

    override suspend fun getExercises(): List<Exercise> = dbQuery {
        Exercises.selectAll().map { resultRowToExercise(it) }
    }

    override suspend fun getExercise(id: Int): Exercise?= dbQuery {
        Exercises.select { (Exercises.id eq id) }.map { resultRowToExercise(it) }.singleOrNull()
    }

    override suspend fun getExercisesByWorkoutId(workoutId: Int): List<Exercise> = dbQuery {
        Exercises.select { (Exercises.workoutId eq workoutId) }.map{resultRowToExercise(it)  }
    }
    override suspend fun removeExercisesByWorkoutId(workoutId:Int): Boolean= dbQuery{
        Exercises.deleteWhere { Exercises.workoutId eq workoutId } >0

    }

}