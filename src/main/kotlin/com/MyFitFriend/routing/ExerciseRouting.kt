package com.MyFitFriend.routing


import com.MyFitFriend.data.model.Exercise
import com.MyFitFriend.data.model.ExerciseEntity
import com.MyFitFriend.exerciseService
import com.MyFitFriend.requests.ExerciseRequest
import com.MyFitFriend.userService
import com.MyFitFriend.workoutService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*

import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.ExercisesRouting(){

    route("/exercises"){


        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                if(id==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                val workoutId=call.request.queryParameters["workoutId"]?.toIntOrNull()
                val exerciseId=call.request.queryParameters["exerciseId"]?.toIntOrNull()

if(workoutId==null && exerciseId==null)
{
call.respond(HttpStatusCode.OK, exerciseService.getExercises())
    return@get

}
                if(exerciseId==null && workoutId!=null) {
                    call.respond(HttpStatusCode.OK, exerciseService.getExercisesByWorkoutId(workoutId))
                    return@get
                }
                else{

                    if(exerciseId==null){
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val exercise = exerciseService.getExercise(exerciseId)
                    if (exercise == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(HttpStatusCode.OK, exercise)
                    }
                }
                }

            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val workoutId=call.request.queryParameters["workoutId"]?:""
                println("DEBUG EXERCISES $workoutId")
                if(id==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
                val exerciseRequest: ExerciseEntity =
                    try {
                        call.receive<ExerciseEntity>()

                    }
                    catch (e: ContentTransformationException){
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                if(workoutId==""){
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
                if(!workoutService.isOwnedByUser(id,workoutId.toInt()))
                {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val checkExerciseInServer=exerciseService.getExercise(exerciseRequest.exerciseId)
                val exercise= Exercise(
                    workoutId=workoutId.toInt(),
                    description =exerciseRequest.description,
                    title=exerciseRequest.title,
                    weights =exerciseRequest.weights,
                    setCount =exerciseRequest.setCount,
                    repCount =exerciseRequest.repCount,
                    restTime =exerciseRequest.restTime,
                    lastEditDate = exerciseRequest.lastEditDate


                )
                if(checkExerciseInServer!=null){
                    println("DEBUG HAVE IN DB")
                    //have in db so update
                    if(checkExerciseInServer.lastEditDate<exerciseRequest.lastEditDate){
                        exerciseService.editExercise(exerciseId = checkExerciseInServer.exerciseId,exercise)
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                    else
                    {
                        call.respond(HttpStatusCode.Accepted)
                        return@post
                    }


                }
                else{
                    //dont have in db add it
                    println("DEBUG DONT HAVE  IN DB")


                    val exerciseAdd=exerciseService.addExercise(exercise)!=null
                    if(exerciseAdd) {
                        call.respond(HttpStatusCode.OK,exerciseAdd )
                        return@post
                    }
                    else{
                        call.respond(HttpStatusCode.BadRequest)

                        return@post}
                }




            }



            patch {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                val workoutId=call.request.queryParameters["workoutId"]?:""
                val exerciseId=call.request.queryParameters["exerciseId"]?:""

                if(id==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@patch
                }
                val exerciseRequest: ExerciseRequest =
                    try {
                        call.receive<ExerciseRequest>()

                    }
                    catch (e: ContentTransformationException){
                        call.respond(HttpStatusCode.BadRequest)
                        return@patch
                    }
                if(workoutId==""||exerciseId==""){
                    call.respond(HttpStatusCode.NotFound)
                    return@patch
                }
                if(!workoutService.isOwnedByUser(id,workoutId.toInt()))
                {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@patch
                }
                val exercise= Exercise(
                    workoutId=workoutId.toInt(),
                    description =exerciseRequest.description,
                    title=exerciseRequest.title,
                    weights =exerciseRequest.weights,
                    setCount =exerciseRequest.setCount,
                    repCount =exerciseRequest.repCount,
                    restTime =exerciseRequest.restTime,
                    lastEditDate = exerciseRequest.lastEditDate


                )
                if(exerciseService.editExercise(exerciseId.toInt(),exercise)) {
                    call.respond(HttpStatusCode.OK )
                    return@patch
                }
                else{
                    call.respond(HttpStatusCode.BadRequest)

                    return@patch}

            }
            delete {
                val email = call.principal<UserIdPrincipal>()!!.name
                val userId = userService.getUserIdByEmail(email)
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "User not found")
                    return@delete
                }

                val workoutId = call.request.queryParameters["workoutId"]
                val exerciseId = call.request.queryParameters["exerciseId"]

                when {
                    // Scenario 1: Delete all exercises for a workout
                    workoutId != null && exerciseId == null -> {
                        if (!workoutService.isOwnedByUser(userId, workoutId.toInt())) {
                            call.respond(HttpStatusCode.Unauthorized)
                        } else if (exerciseService.removeExercisesByWorkoutId(workoutId.toInt())) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }

                    // Scenario 2: Delete a specific exercise
                    exerciseId != null -> {
                         if (exerciseService.removeExercise(exerciseId.toInt())) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    // Error handling if neither nor both parameters are provided
                    else -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
                    }
                }
            }


        }


        }

    }

