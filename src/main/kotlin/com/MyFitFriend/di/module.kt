package com.MyFitFriend.di


import com.MyFitFriend.db.*
import org.koin.dsl.module

val appModule= module {
    single<UserService> {
        UserServiceIMPL()
    }
    single<DietaryLogService> {
        DietaryLogServiceIMPL()
    }
    single<DietGroupService>{
        DietGroupServiceIMPL()
    }
    single<ExerciseService>{
        ExerciseServiceIMPL()
    }
    single<FoodService>{
        FoodServiceIMPL()
    }
    single<WorkoutService>{
        WorkoutServiceIMPL()
    }


}