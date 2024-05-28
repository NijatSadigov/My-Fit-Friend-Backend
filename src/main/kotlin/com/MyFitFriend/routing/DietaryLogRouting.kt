package com.MyFitFriend.routing

import com.MyFitFriend.data.model.DietaryLog
import com.MyFitFriend.data.model.DietaryLogEntity
import com.MyFitFriend.data.model.Exercise

import com.MyFitFriend.dietaryLogService
import com.MyFitFriend.foodService
import com.MyFitFriend.requests.DietaryLogRequest
import com.MyFitFriend.userService
import io.ktor.client.engine.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Route.DietaryLogRouting (){


    route("/dietarylog"){


        authenticate {
          get {
            val email=call.principal<UserIdPrincipal>()!!.name
            val id=userService.getUserIdByEmail(email)
              /////to get spesific data
              val date=call.request.queryParameters["date"]?:""
              val partOfDay=call.request.queryParameters["partOfDay"]?.toIntOrNull()
              val wantedUserId=call.request.queryParameters["wantedUserId"]?.toIntOrNull()
              val dietaryLogId=call.request.queryParameters["dietaryLogId"]?.toIntOrNull()
             if(wantedUserId!=null){
                 val today= LocalDate.now().toString()

                 val breakFastLogs=(dietaryLogService.getDietaryLogByDateAndPartOfDay(wantedUserId,today,0))
                 val lunchFastLogs=(dietaryLogService.getDietaryLogByDateAndPartOfDay(wantedUserId,today,1))
                 val dinnerFastLogs=(dietaryLogService.getDietaryLogByDateAndPartOfDay(wantedUserId,today,2))
                 val snackFastLogs=(dietaryLogService.getDietaryLogByDateAndPartOfDay(wantedUserId,today,3))

                 call.respond(HttpStatusCode.OK, (breakFastLogs+lunchFastLogs+dinnerFastLogs+snackFastLogs))
                     return@get

             }

              else if (id != null) {
                  if(dietaryLogId!=null){
                      val log=dietaryLogService.getDietaryLog(dietaryLogId)
                      if(log!=null) {
                          call.respond(HttpStatusCode.OK,log)
                          return@get
                      }
                      else{
                          call.respond(HttpStatusCode.BadRequest)
                          return@get
                      }
                  }
                  if(date!="" &&partOfDay!=null  ){
                  call.respond(HttpStatusCode.OK, dietaryLogService.getDietaryLogByDateAndPartOfDay(id,date,partOfDay))
                    return@get
                  }

               call.respond(HttpStatusCode.OK, dietaryLogService.getDietaryLogsByUserId(id))
                return@get
            }
            else{call.respond(HttpStatusCode.Unauthorized)
                return@get}
    }

            post{
                //add food
                val email=call.principal<UserIdPrincipal>()!!.name
                val id=userService.getUserIdByEmail(email)
                val dietaryLogRequest:DietaryLogEntity=
                    try {
                        call.receive<DietaryLogEntity>()

                    }
                    catch (e: ContentTransformationException){
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                if(id==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }


                val log=dietaryLogService.getDietaryLog(dietaryLogRequest.dietaryLogId)
                if (log!= null && log.userId==id) {
                    //have food in db, so UPDATE
                    if(log.lastEditDate<dietaryLogRequest.lastEditDate){
                        val dietaryLog = DietaryLog(
                            userId = id,
                            date = dietaryLogRequest.date,
                            amountOfFood = dietaryLogRequest.amountOfFood,
                            foodItem = dietaryLogRequest.foodItem,
                            partOfDay = dietaryLogRequest.partOfDay,
                            foodId = dietaryLogRequest.foodId,
                            lastEditDate = dietaryLogRequest.lastEditDate
                        )
                        dietaryLogService.editDietaryLog(dietaryLogRequest.dietaryLogId, dietaryLog)
                        call.respond(HttpStatusCode.OK)
                    }
                    else call.respond(HttpStatusCode.Accepted)

                }
                else {

                    val foodItem= foodService.getFood(dietaryLogRequest.foodId)!!.foodName
                    val dietaryLog =DietaryLog(
                        userId= id,
                        date= LocalDate.now().toString(),
                        amountOfFood = dietaryLogRequest.amountOfFood,
                        foodItem = foodItem,
                        partOfDay = dietaryLogRequest.partOfDay,
                        foodId= dietaryLogRequest.foodId,
                        lastEditDate =  System.currentTimeMillis()
                    )






                    val addedLog= dietaryLogService.addDietaryLog(dietaryLog)
                    if(addedLog!=null ) {
                        call.respond(HttpStatusCode.OK,addedLog)
                        return@post
                    }
                    else
                        call.respond(HttpStatusCode.BadRequest)
                }


            }


            delete{

                val email=call.principal<UserIdPrincipal>()!!.name
                val id=userService.getUserIdByEmail(email)
                val logId=call.request.queryParameters["id"]?:""
                if(logId=="" ||id==null ){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val logIdInt:Int
                try {
                    logIdInt = logId.toInt()
                }
                catch (e:NumberFormatException){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if(!dietaryLogService.isOwnedByUser(id,logIdInt))
                {
                    call.respond(HttpStatusCode.Unauthorized    )
                    return@delete
                }
                    if(dietaryLogService.removeDietaryLog(logIdInt)) {
                        call.respond(HttpStatusCode.OK)
                        return@delete
                    }
                else {
                    call.respond(HttpStatusCode.BadRequest)
                        return@delete
                }

            }

            patch {
                val email=call.principal<UserIdPrincipal>()!!.name
                val id=userService.getUserIdByEmail(email)
                val logId=call.request.queryParameters["id"]?:""
                if(logId=="" ||id==null ){
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }

                    val dietaryLogRequest:DietaryLogRequest=
                        try {
                            call.receive<DietaryLogRequest>()

                        }
                        catch (e: ContentTransformationException){
                            call.respond(HttpStatusCode.BadRequest)
                            return@patch
                        }
                if(!dietaryLogService.isOwnedByUser(id,logId.toInt()))
                {
                    call.respond(HttpStatusCode.Unauthorized    )
                    return@patch
                }
                val foodItem= foodService.getFood(dietaryLogRequest.foodId)!!.foodName

                val dietaryLog =DietaryLog(
                    userId= id,
                    date= LocalDate.now().toString(),
                    amountOfFood = dietaryLogRequest.amountOfFood,
                    foodItem = foodItem,
                    partOfDay = dietaryLogRequest.partOfDay,
                    foodId= dietaryLogRequest.foodId,
                    lastEditDate =  System.currentTimeMillis()

                )
               if( dietaryLogService.editDietaryLog(logId.toInt(),dietaryLog)){
                   call.respond(HttpStatusCode.OK)
                   return@patch
               }
                else{
                    call.respond(HttpStatusCode.BadRequest)
                   return@patch
               }

            }
    }

}
}