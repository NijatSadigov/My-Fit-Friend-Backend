package com.MyFitFriend.db

import com.MyFitFriend.data.info.checkHashForPassword
import com.MyFitFriend.data.model.User
import com.MyFitFriend.data.model.UserEntity
import com.MyFitFriend.data.model.Users
import com.MyFitFriend.plugins.dbQuery
import com.MyFitFriend.requests.userEditRequest
import org.jetbrains.exposed.sql.*

class UserServiceIMPL:UserService {
    private fun resultRowToUser(row: ResultRow):User{
        return User(
            userId = row[Users.userId],
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
             weight = row[Users.weight],
             height =row[Users.height],
             activityLevel =row[Users.activityLevel],
            age=row[Users.age],
            sex=row[Users.sex],
            lastEditDate = row[Users.lastEditDate]
        )
    }

    override suspend fun addUser(user: User): Boolean  = dbQuery {
try {


    val insertStmt = Users.insert {
        it[username] = user.username
        it[email] = user.email
        it[passwordHash] = user.passwordHash
        it[weight] = user.weight
        it[height] = user.height
        it[activityLevel] = user.activityLevel
        it[age]=user.age
        it[sex]=user.sex
        it[lastEditDate]=System.currentTimeMillis()
    }
    insertStmt.insertedCount>0
}
    catch(e: Exception) {
        // Handle potential errors such as connection issues, constraint violations, etc.
        e.printStackTrace()
        false
    }



    }

    override suspend fun removeUser(userId: Int): Boolean = dbQuery{
        Users.deleteWhere { Users.userId eq userId } >0

            }

    override suspend fun editUser(userId: Int, user: userEditRequest): Boolean = dbQuery{
        Users.update({Users.userId eq userId}){
            it[username]=user.username
            it[weight]=user.weight
            it[height]=user.height
            it[activityLevel]=user.activityLevel
            it[age]=user.age
            it[sex]=user.sex
            it[sex]=user.sex
            it[lastEditDate]=user.lastEditDate
        }>0    }

    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map { resultRowToUser(it) }
    }

    override suspend fun getUser(userId: Int): User? = dbQuery{
        Users.select { (Users.userId eq userId) }.map { resultRowToUser(it) }.singleOrNull()


    }

    override suspend fun getUserIdByEmail(email: String): Int? = dbQuery{
        val user = Users.select { (Users.email eq email) }.map { resultRowToUser(it) }.singleOrNull()

        user?.userId
    }

    override suspend fun checkUserPasswordByEmail(email: String, password: String): Boolean = dbQuery {
        val user = Users.select { (Users.email eq email) }.map { resultRowToUser(it) }.singleOrNull()
        val actualPassword=user?.passwordHash ?: ""
       if(user==null){
           false
       }
        else
        checkHashForPassword(password, actualPassword)
    }

    override suspend fun emailAlreadyUsed(email: String): Boolean= dbQuery{
        Users.select { Users.email eq email }
            .any()
    }
}