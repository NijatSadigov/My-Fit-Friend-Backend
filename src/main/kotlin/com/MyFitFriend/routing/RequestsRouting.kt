package com.MyFitFriend.routing

import com.MyFitFriend.dietGroupService
import com.MyFitFriend.userService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.RequestsRouting(){
    route("/grouprequests"){
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                if(id==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                try {
                    call.respond(HttpStatusCode.OK, dietGroupService.getRequestsByUserId(id ))
                }
                catch (
                    e:Exception
                ){
                    call.respond(HttpStatusCode.BadRequest,e)
                }


            }


            post{
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val answer=call.request.queryParameters["answer"]?.toBooleanStrictOrNull()
                val requestId=call.request.queryParameters["requestId"]?.toIntOrNull()


                if(userId==null || answer==null ||requestId==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                if (dietGroupService.answerDeleteRequest(answer,requestId,userId)){
                    call.respond(HttpStatusCode.OK)
                }
                else
                    call.respond(HttpStatusCode.BadRequest)





            }


        }
    }



}