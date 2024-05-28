package com.MyFitFriend.db

import com.MyFitFriend.data.model.User
import com.MyFitFriend.requests.userEditRequest

interface UserService {
   suspend fun addUser(user:User):Boolean
   suspend fun removeUser(userId :Int):Boolean
   suspend fun editUser(userId :Int , user: userEditRequest):Boolean
   suspend fun getUsers():List<User>
   //suspend fun searchUser(query:String):List<User>
   suspend fun getUser(userId:Int):User?
   suspend fun getUserIdByEmail(email:String):Int?
   suspend fun checkUserPasswordByEmail(email: String, password:String):Boolean
   suspend fun emailAlreadyUsed(email: String):Boolean
}