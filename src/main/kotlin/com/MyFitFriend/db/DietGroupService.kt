package com.MyFitFriend.db

import com.MyFitFriend.data.info.AddFriendRequestInfo
import com.MyFitFriend.data.model.DietGroup

interface DietGroupService {
    //manage group
    suspend fun createDietGroup(group: DietGroup): Int


    suspend fun getDietGroupById(groupId: Int): DietGroup?


    suspend fun updateDietGroupName(groupId: Int, newName: String): DietGroup?


    suspend fun deleteDietGroup(groupId: Int): Boolean
//manage auths and checkers
suspend fun isUserOwner(userId: Int,groupId: Int):Boolean
    suspend fun doMemberExistAlready(userId: Int,groupId: Int):Boolean
// manage users
    suspend  fun addUserToDietGroup(userId: Int, groupId: Int): Boolean
    suspend fun removeUserFromDietGroup(userId: Int, groupId: Int): Boolean

    suspend fun getUserDietGroups(userId: Int): List<DietGroup>
    suspend fun getUsersOfGroupByGroupId(groupId: Int):List<Int>
//
    suspend fun createRequest(wantedUserId:Int, groupId: Int):Boolean
    suspend fun answerDeleteRequest(answer:Boolean,requestId:Int,wantedUserId:Int):Boolean
    suspend fun removeAllRequestForGroup(groupId: Int):Boolean
    suspend fun getRequestsByUserId(userId: Int):List<AddFriendRequestInfo>

    suspend fun hasAlreadyInvited(wantedUserId:Int,dietGroupId:Int):Boolean


}
