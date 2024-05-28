package com.MyFitFriend.plugins

import com.MyFitFriend.db.DietaryLogService
import com.MyFitFriend.db.UserService
import com.MyFitFriend.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
fun Application.configureRouting() {
    routing {
        UserRouting()
        DietaryLogRouting()
        WorkoutRouting()
        ExercisesRouting()
        FoodRouting()
        DietGroupRouting()
        RequestsRouting()
    }

}
