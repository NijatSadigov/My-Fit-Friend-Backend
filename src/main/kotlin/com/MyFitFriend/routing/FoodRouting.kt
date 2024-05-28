package com.MyFitFriend.routing

import com.MyFitFriend.foodService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.FoodRouting() {
    route("/foods"){
        get{
            // If there is an 'id' query parameter, handle it; otherwise, get all foods.
            val foodIdQuery = call.request.queryParameters["id"]?.toIntOrNull()
            val foodqr = call.parameters["qrCode"]



            if (foodIdQuery != null) {
                val food = foodService.getFood(foodIdQuery)
                if (food != null) {
                    call.respond(HttpStatusCode.OK, food)
                } else {
                    call.respondText("No food found with ID: $foodIdQuery", status = HttpStatusCode.NotFound)
                }
            }
            else if(foodqr!=null){
                val foodId= foodService.getFoodIdByQR(foodqr)
                println("foorId:$foodId foodqr:$foodqr")
                if(foodId==null){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                val food = foodService.getFood(foodId)

                if (food != null) {
                    call.respond(HttpStatusCode.OK, food)
                } else {
                    call.respondText("No food found with ID: $foodqr", status = HttpStatusCode.NotFound)
                }
            }
            else {
                call.respond(HttpStatusCode.OK, foodService.getAllFoods())
            }
        }


    }


}