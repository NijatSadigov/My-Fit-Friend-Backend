package com.MyFitFriend.routing

import com.MyFitFriend.data.model.Workout
import com.MyFitFriend.data.model.WorkoutEntity

import com.MyFitFriend.requests.WorkoutRequest
import com.MyFitFriend.userService
import com.MyFitFriend.workoutService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import kotlin.math.log

fun Route.WorkoutRouting(){
    route("workout"){
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val workoutIdS = call.request.queryParameters["workoutId"] ?: ""

                if (workoutIdS == "") {
                    if (id != null) {
                        call.respond(HttpStatusCode.OK, workoutService.getAllWorkoutsByUserId(id))
                        return@get
                    } else {
                        println("$id")

                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }
                }

                val workoutId = workoutIdS.toIntOrNull()
                if (workoutId != null) {
                    val workout = workoutService.getWorkout(workoutId)
                    if (workout != null) {
                        val workoutAsEntity= WorkoutEntity(
                            lastEditDate = workout.lastEditDate,
                            date = workout.date,
                            title = workout.title,
                            userId = workout.userId,
                            description = workout.description,
                            isSync = true,
                            isAdded = true,
                            workoutId = workout.workoutId
                        )
                        call.respond(HttpStatusCode.OK, workoutAsEntity)
                        return@get
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Workout not found")
                        return@get
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid workout ID")
                    return@get
                }
            }

            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val workoutRequest: WorkoutEntity =
                    try {
                        call.receive<WorkoutEntity>()

                    }
                    catch (e: ContentTransformationException){
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                if(id==null){
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }



                val log= workoutService.getWorkout(workoutRequest.workoutId)
                if (log!= null && log.userId==id) {
                    //have food in db, so UPDATE
                    if(log.lastEditDate<workoutRequest.lastEditDate){
                        val workout = Workout(
                            userId = id,
                            date = workoutRequest.date,
                            lastEditDate = workoutRequest.lastEditDate,
                            description = workoutRequest.description,
                            title = workoutRequest.title

                        )
                        workoutService.editWorkout(workoutRequest.workoutId, workout)

                        val finalWorkout=workoutService.getWorkout(workoutRequest.workoutId)
                        if(finalWorkout!=null) {
                            call.respond(HttpStatusCode.OK, finalWorkout)
                            return@post
                        }
                    }
                    else {
                        val finalWorkout=workoutService.getWorkout(workoutRequest.workoutId)
                        if(finalWorkout!=null)
                        call.respond(HttpStatusCode.Accepted,finalWorkout)
                    }
                }
                else {

                    val workout =Workout(
                        userId = id,
                        date = workoutRequest.date,
                        lastEditDate = workoutRequest.lastEditDate,
                        description = workoutRequest.description,
                        title = workoutRequest.title

                    )
                    val addedLog= workoutService.createWorkout(workout)
                    if(addedLog!=null ) {
                        call.respond(HttpStatusCode.OK,addedLog)
                        return@post
                    }
                    else
                        call.respond(HttpStatusCode.BadRequest)
                }

            }
            patch{
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val logId=call.request.queryParameters["workoutId"]?:""

                val workoutRequest: WorkoutRequest =
                    try {
                        call.receive<WorkoutRequest>()

                    }
                    catch (e: ContentTransformationException){
                        call.respond(HttpStatusCode.BadRequest)
                        return@patch
                    }

                if(id==null || logId==""){
                    call.respond(HttpStatusCode.NotFound)
                    return@patch
                }

                if(!workoutService.isOwnedByUser(id.toInt(),logId.toInt()))
                {
                    println("$id and $logId")

                    call.respond(HttpStatusCode.Unauthorized    )
                    return@patch
                }
                val workout=Workout(
                userId=id.toInt(),
                date= LocalDate.now().toString(),
                title=workoutRequest.title,
                description =workoutRequest.description,
                    lastEditDate = workoutRequest.lastEditDate

                )
                if(workoutService.editWorkout(logId.toInt(),workout)!=null)
                {
                    call.respond(HttpStatusCode.OK)
                    return@patch
                }
                else{
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }
            }

            delete{
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val logId=call.request.queryParameters["workoutId"]?:""
                println("DELETEWORKOUT:: ${logId}")
                if(id==null || logId==""){
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                if(!workoutService.isOwnedByUser(id.toInt(),logId.toInt()))
                {
                    println("$id and $logId")
                    call.respond(HttpStatusCode.Unauthorized    )
                    return@delete
                }

                if(workoutService.removeWorkout(logId.toInt()))
                {
                    println("DELETEWORKOUT REMOVED:: ${logId}")

                    call.respond(HttpStatusCode.OK)
                    return@delete
                }
                else{
                    println("DELETEWORKOUT NOT REMOVED:: ${logId}")

                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
            }



        }
    }
}