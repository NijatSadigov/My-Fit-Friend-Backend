package com.MyFitFriend.routing

import com.MyFitFriend.dietGroupService

import com.MyFitFriend.userService
import com.MyFitFriend.data.model.DietGroup
import com.MyFitFriend.db.DietGroupService
import com.MyFitFriend.dietaryLogService
import com.MyFitFriend.foodService
import com.example.myfitfriend.data.remote.reponses.GroupDietaryLogsItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Route.DietGroupRouting() {

    route("/dietgroup"){
        authenticate {
            get {

                //get user's groups
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val groupUserId=call.request.queryParameters["groupUserId"]?.toIntOrNull()
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()
                val doYouWantGroupDietaryLogItem=call.request.queryParameters["doYouWantGroupDietaryLogItem"]?.toBooleanStrictOrNull()

        if(doYouWantGroupDietaryLogItem==null ||doYouWantGroupDietaryLogItem==false ){
                    if (groupId != null) {
                        //getting dietGroup
                        val groupMembers = dietGroupService.getDietGroupById(groupId)
                        if (groupMembers != null) {
                            call.respond(HttpStatusCode.OK, groupMembers)
                            return@get
                        } else {
                            call.respond(HttpStatusCode.BadRequest)
                            return@get
                        }
                    }

                    if (groupUserId != null) {
                        //getting UserOfGroup
                        val user = userService.getUser(groupUserId)
                        if (user != null) {
                            call.respond(HttpStatusCode.OK, user)
                            return@get
                        } else {
                            call.respond(HttpStatusCode.BadRequest)
                            return@get
                        }
                    }
                    if (userId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    try {
                        //get groups
                        println(userId)
                        call.respond(HttpStatusCode.OK, dietGroupService.getUserDietGroups(userId))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest)

                    }
                }

        else{
            if(groupId!=null){
                try {
                    val memberIds = dietGroupService.getUsersOfGroupByGroupId(groupId)
                    val groupsLogsItems=mutableListOf<GroupDietaryLogsItem>()
                    val date=LocalDate.now()
                    memberIds.forEach(){

                        val user=userService.getUser(it)
                        val dietLogs= dietaryLogService.getDietaryLogByDate(userId=it,date=date.toString())
                        var totalCal:Double=0.0
                        var totalProtein:Double=0.0
                        var totalCarb:Double=0.0
                        var totalFat:Double=0.0
                        dietLogs.forEach(){
                            dietLog->
                            val food = foodService.getFood(dietLog.foodId)
                            totalCal+=food!!.cal*dietLog.amountOfFood/100
                            totalProtein+=food.protein*dietLog.amountOfFood/100
                            totalCarb+=food.carb*dietLog.amountOfFood/100
                            totalFat+=food.fat*dietLog.amountOfFood/100

                        }
                        //
                        val bmr = if (user!!.sex) {
                            10 * user.weight + 6.25 * user.height - 5 * user.age + 5
                        } else {
                            10 * user.weight + 6.25 * user.height - 5 * user.age - 161
                        }

                        // Activity level factors
                        val activityFactors = mapOf(
                            0 to 1.2,       // Sedentary
                            1 to 1.375,     // Light Activity
                            2 to 1.55,      // Medium Activity
                            3 to 1.725      // Hard Activity
                        )

                        // Calculate Total Daily Energy Expenditure (TDEE)
                        val tdee = bmr * (activityFactors[user.activityLevel] ?: 1.2) // Default to sedentary if unknown activity level



                        //
                        groupsLogsItems.add(GroupDietaryLogsItem(userId=user.userId,
                            userName = user.username,
                            totalCalories = totalCal,
                            totalFat = totalFat,
                            totalCarb = totalCarb,
                            totalProtein = totalProtein,
                            maxCalories = tdee))

                    }
                    call.respond(HttpStatusCode.OK,groupsLogsItems)
                    return@get
                }
                catch (e:Exception){
                    println(e)
                    call.respond(HttpStatusCode.BadRequest,e)
                    return@get
                }
            }
            else{
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            /*  val userName:String, //
    val userId:Int,//
    val maxCalories:Double,//
    val totalCalories:Double,
    val totalProtein:Double,
    val totalCarb:Double,
    val totalFat:Double

*/

        }
            }
            post {
               //create and add owner to group
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val newGroupName=call.request.queryParameters["groupname"]


                if(userId==null || newGroupName==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                //println("data getted")
              //  println(newGroupName)
                val dietGroup= DietGroup(
                groupOwnerId=userId,
                groupName =newGroupName,
                    groupId = null
                    )
                val createdGroupId=dietGroupService.createDietGroup(dietGroup)
                //println(createdGroupId)
                    if(dietGroupService.addUserToDietGroup(userId,createdGroupId))
                        call.respond(HttpStatusCode.OK)
                    else
                        call.respond(HttpStatusCode.BadRequest)
//                println("User added successfully")

            }
            delete {
                //delete group by group id
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()
                if(userId==null|| groupId==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if(!dietGroupService.isUserOwner(userId, groupId ))
                {
                    println("dietgroupService $userId -$groupId")
                    call.respond(HttpStatusCode.Unauthorized)
                    return@delete
                }

                    dietGroupService.removeAllRequestForGroup(groupId)
                    dietGroupService.deleteDietGroup(groupId)
                    call.respond(HttpStatusCode.OK,groupId)

                    call.respond(HttpStatusCode.BadRequest)



            }
            patch {
               //change group name by group id and groupName
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()
                val groupName=call.request.queryParameters["groupname"]

                if(userId==null|| groupId==null || groupName==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }
                if(!dietGroupService.isUserOwner(userId, groupId ))
                {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@patch
                }
                val editedGroup = dietGroupService.updateDietGroupName(groupId,groupName)
                if(editedGroup!=null){
                    call.respond(HttpStatusCode.OK)
                }
                else{
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

            get("/members"){
               //see members of spesific group by group id
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()
                println("groupId $groupId")
                if( groupId==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {


                   call.respond(HttpStatusCode.OK ,dietGroupService.getUsersOfGroupByGroupId(groupId))
                }
                catch (e:Exception){
                    application.log.error("Error retrieving members for group $groupId: ${e.message}")

                    call.respond(HttpStatusCode.BadRequest)

                }

            }
            post("/members"){
                //add users
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val wantedUserEmail=call.request.queryParameters["wantedUserEmail"] ?:""
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()

                val wantedUserId= userService.getUserIdByEmail(wantedUserEmail)?.toInt() ?: -1

                if(userId==null || wantedUserId==-1 || groupId==null){
                    call.respond(HttpStatusCode.NotFound     )
                    return@post
                }

                if(!dietGroupService.isUserOwner(userId, groupId ))
                {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                if(!dietGroupService.doMemberExistAlready(wantedUserId,groupId))
                {
                    println(dietGroupService.hasAlreadyInvited(wantedUserId=wantedUserId, dietGroupId = groupId))
                    if(dietGroupService.hasAlreadyInvited(wantedUserId=wantedUserId, dietGroupId = groupId)){
                        call.respond(HttpStatusCode.NotAcceptable)
                        return@post
                    }


                    if (dietGroupService.createRequest(wantedUserId,groupId))
                        call.respond(HttpStatusCode.OK)
                    else
                        call.respond(HttpStatusCode.BadRequest)
                }
                else{
                    call.respond(HttpStatusCode.Conflict)

                }

            }
            delete("/members") {
                //delete member from group by id
                val email=call.principal<UserIdPrincipal>()!!.name
                val userId= userService.getUserIdByEmail(email)
                val wantedUserId=call.request.queryParameters["wanteduserid"]?.toIntOrNull()
                val groupId=call.request.queryParameters["groupId"]?.toIntOrNull()


                if(userId==null || wantedUserId==null || groupId==null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if(!dietGroupService.isUserOwner(userId, groupId ))
                {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@delete
                }

                if(dietGroupService.doMemberExistAlready(wantedUserId,groupId))
                {
                    if (dietGroupService.removeUserFromDietGroup(wantedUserId,groupId))
                        call.respond(HttpStatusCode.OK)
                    else
                        call.respond(HttpStatusCode.BadRequest)
                }
                else{
                    call.respond(HttpStatusCode.Conflict)
                }

            }








        }



    }



}