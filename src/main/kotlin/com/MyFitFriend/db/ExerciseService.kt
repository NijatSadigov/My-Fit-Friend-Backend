package com.MyFitFriend.db

import com.MyFitFriend.data.model.Exercise


interface ExerciseService {
    suspend fun addExercise( exercise: Exercise):Exercise?
    suspend fun removeExercise(exerciseId :Int):Boolean
    suspend fun editExercise(exerciseId :Int , exercise: Exercise):Boolean
    suspend fun getExercises():List<Exercise>
    //suspend fun searchWorkout(query:String):List<User>
    suspend fun getExercise(id:Int): Exercise?
    suspend fun getExercisesByWorkoutId(workoutId:Int): List<Exercise>
    suspend fun removeExercisesByWorkoutId(workoutId:Int): Boolean
}