package com.MyFitFriend.db

import com.MyFitFriend.data.model.User
import com.MyFitFriend.data.model.Workout

interface WorkoutService {
    suspend fun createWorkout( workout: Workout):Workout?
    suspend fun removeWorkout(workoutId :Int):Boolean
    suspend fun editWorkout(workoutId :Int , workout: Workout):Workout?
    suspend fun getWorkouts():List<Workout>
    //suspend fun searchWorkout(query:String):List<User>
    suspend fun getWorkout(id:Int): Workout?
    suspend fun getAllWorkoutsByUserId(userId:Int):List<Workout>
    suspend fun isOwnedByUser(userId: Int,workoutId: Int):Boolean

}