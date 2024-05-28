package com.MyFitFriend.routing

import com.MyFitFriend.data.info.getHashWithSalt
import com.MyFitFriend.data.model.User
import com.MyFitFriend.db.UserService
import com.MyFitFriend.dietGroupService
import com.MyFitFriend.requests.UserLoginRequest
import com.MyFitFriend.requests.UserRegisterRequest
import com.MyFitFriend.requests.userEditRequest
import com.MyFitFriend.userService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.UserRouting(){

    route("/register"){

        post {
            val user = try {
            call.receive<User>()
            }
            catch (e:ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val emailPattern = ("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$").toRegex()
            if(!emailPattern.matches (user.email)){
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            if(userService.emailAlreadyUsed(user.email)){
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            val hashPassword= getHashWithSalt(user.passwordHash)
            val userToAdd= User(username = user.username,
            email=user.email,
                weight = user.weight,
                height = user.height,
                activityLevel = user.activityLevel,
                passwordHash = hashPassword,
                age=user.age,
                sex=user.sex,
                lastEditDate = System.currentTimeMillis()
            )
            if(userService.addUser(userToAdd)){
                call.respond(HttpStatusCode.OK)
                return@post
            }
            else {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

        }


    }
    route("/login"){


        post {
            val user = try {
                call.receive<UserLoginRequest>()
            }
            catch (e:ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if(userService.checkUserPasswordByEmail(user.email,user.passwordHash)){
                call.respond(HttpStatusCode.OK)
                return@post
            }
            else{
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
        }



    }

    route("/profile"){
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val id = userService.getUserIdByEmail(email)
                if(id==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                val user=userService.getUser( id )
                if(user==null ){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                try {
                    call.respond(HttpStatusCode.OK,user )
                }
                catch (
                    e:Exception
                ){
                    call.respond(HttpStatusCode.BadRequest,e)
                }


            }

        patch {
            val email = call.principal<UserIdPrincipal>()!!.name
            val id = userService.getUserIdByEmail(email)
            if(id==null ){
                call.respond(HttpStatusCode.NotFound)
                return@patch
            }

            val user = try {
                call.receive<userEditRequest>()
            }
            catch (e:ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }

            try {
                val currentUserData= userService.getUser(id)
                if(currentUserData!=null &&currentUserData.lastEditDate<user.lastEditDate) {
                    if (userService.editUser(id, user))
                        call.respond(HttpStatusCode.OK)
                    else
                        call.respond(HttpStatusCode.BadRequest)
                }
                else{
                    call.respond(HttpStatusCode.Accepted)
                }
            }
            catch (
                e:Exception
            ){
                call.respond(HttpStatusCode.BadRequest,e)
            }

        }




        }

    }
}