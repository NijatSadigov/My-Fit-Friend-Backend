package com.MyFitFriend

import com.MyFitFriend.db.*
import com.MyFitFriend.plugins.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
val userService:UserService=UserServiceIMPL()
val dietaryLogService:DietaryLogService=DietaryLogServiceIMPL()
val exerciseService:ExerciseService=ExerciseServiceIMPL()
val workoutService:WorkoutService=WorkoutServiceIMPL()
val foodService:FoodService=FoodServiceIMPL()
val dietGroupService:DietGroupService=DietGroupServiceIMPL()
fun Application.module() {
    install(DefaultHeaders)
    configureDatabases()
    configureDI()
    configureSerialization()
    install (Authentication){
        configureAuth()
    }
    configureRouting()

    install(CallLogging){
        level  = Level.INFO
    }




}
private fun AuthenticationConfig.configureAuth(){
    basic {
        realm = "My Fit Friend"
        validate { credential ->
            val email = credential.name
            val password = credential.password

            if ( userService.checkUserPasswordByEmail(email,password)) {

                UserIdPrincipal(email)
            } else {
                null
            }
        }
    }
}


