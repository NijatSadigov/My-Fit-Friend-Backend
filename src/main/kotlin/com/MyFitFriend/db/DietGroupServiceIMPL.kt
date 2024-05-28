package com.MyFitFriend.db


import com.MyFitFriend.data.info.AddFriendRequestInfo
import com.MyFitFriend.data.model.*
import com.MyFitFriend.plugins.dbQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.SQLException
import kotlin.math.absoluteValue

class DietGroupServiceIMPL:DietGroupService {
    private fun resultRowToGroup(row: ResultRow): DietGroup {
        return DietGroup(
            groupName = row[DietGroups.groupName],
            groupOwnerId=row[DietGroups.groupOwnerId],
            groupId = row[DietGroups.id].value

        )
    }
    override suspend fun createDietGroup(group: DietGroup): Int= dbQuery {
        try {
            val insertedGroupId = DietGroups.insertAndGetId {
                it[groupName] = group.groupName
                it[groupOwnerId] = group.groupOwnerId
            }.value

            // Attempt to add the group owner as a member using the retrieved ID
            //addUserToDietGroup(group.groupOwnerId, insertedGroupId)
            insertedGroupId
        } catch (e: Exception) {
            val logger = LoggerFactory.getLogger(javaClass)
            logger.error("Failed to create diet group or add owner", e)
            0
        }

    }

    override suspend fun getDietGroupById(groupId: Int): DietGroup? = dbQuery{
       DietGroups.select{(DietGroups.id eq groupId)}.map{resultRowToGroup(it)}.singleOrNull()
    }

    override suspend fun updateDietGroupName(groupId: Int, newName: String): DietGroup? = dbQuery {
        DietGroups.update  ({ DietGroups.id eq groupId }){
            it[groupName]=newName
        }
         DietGroups.select{(DietGroups.id eq groupId)}.map{resultRowToGroup(it)}.singleOrNull()

    }

    override suspend fun deleteDietGroup(groupId: Int): Boolean = dbQuery {
        DietGroupMembers.deleteWhere { DietGroupMembers.dietGroupId eq groupId }
        DietGroups.deleteWhere { DietGroups.id eq groupId } >0
    }

    override suspend fun addUserToDietGroup(userId: Int, groupId: Int): Boolean= dbQuery {
        val logger = LoggerFactory.getLogger(javaClass)
        println("add user: " + userId + "\n to group: groupId")
        try {
            DietGroupMembers.insert {
                it[groupMemberId] = userId
                it[dietGroupId] = groupId
            }
            true
        } catch (e: Exception) {  // Simplified exception handling if specific actions are not needed
            logger.error("Error when adding user $userId to group $groupId", e)
            false
        }
    }



    override suspend fun removeUserFromDietGroup(userId: Int, groupId: Int): Boolean = dbQuery{
         DietGroupMembers.deleteWhere {
            (DietGroupMembers.groupMemberId eq userId) and (DietGroupMembers.dietGroupId eq groupId)
        }>0
    }

    override suspend fun getUserDietGroups(userId: Int): List<DietGroup> = dbQuery {
    //DietGroupMembers.select{(DietGroupMembers.groupMemberId eq userId)}.map { resultRowToGroup(it) }
            // Perform a join between DietGroupMembers and DietGroups tables
        (DietGroupMembers innerJoin DietGroups)
            .slice(DietGroups.id, DietGroups.groupName, DietGroups.groupOwnerId)
            .select { DietGroupMembers.groupMemberId eq userId }
            .map { resultRowToGroup(it) }
    }


    override suspend fun getUsersOfGroupByGroupId(groupId: Int): List<Int> = dbQuery{
        dbQuery {
            DietGroupMembers
                .select { DietGroupMembers.dietGroupId eq groupId }
                .map { it[DietGroupMembers.groupMemberId] }
        }
    }



    override suspend fun isUserOwner(userId: Int,groupId: Int): Boolean = dbQuery{
        DietGroups.select {
            (DietGroups.groupOwnerId eq userId) and (DietGroups.id eq groupId)
        }.singleOrNull() != null  // Return true if there is at least one result
        }

    override suspend fun doMemberExistAlready(userId: Int, groupId: Int): Boolean = dbQuery {
        DietGroupMembers.select {
            (DietGroupMembers.groupMemberId eq userId) and (DietGroupMembers.dietGroupId eq groupId)
        }.singleOrNull() != null
    }
/////////////////////

    override suspend fun createRequest(wantedUserId: Int, groupId: Int): Boolean= dbQuery {
    DietGroupRequests.insert {
        it[DietGroupRequests.wantedUserId] = wantedUserId
        it[DietGroupRequests.groupId] = groupId
    }.insertedCount>0

    }

    override suspend fun answerDeleteRequest(answer: Boolean, requestId: Int, wantedUserId: Int): Boolean = dbQuery {
        if(answer){

            try {
                val resultRow = DietGroupRequests.select(DietGroupRequests.id eq requestId ).singleOrNull()
                if(resultRow!=null){
                val groupId=resultRow[DietGroupRequests.groupId]

                DietGroupMembers.insert {
                    it[groupMemberId] = wantedUserId
                    it[dietGroupId] = groupId
                }
                }
            } catch (e: Exception) {  // Simplified exception handling if specific actions are not needed
                println(e)
            }
        }
        DietGroupRequests.deleteWhere { (DietGroupRequests.id eq requestId) }>0


    }


    override suspend fun removeAllRequestForGroup(groupId: Int): Boolean = dbQuery {
        DietGroupRequests.deleteWhere { (DietGroupRequests.groupId eq groupId) }>0
    }

    override suspend fun getRequestsByUserId(userId: Int): List<AddFriendRequestInfo> = dbQuery{
        val groupIds =DietGroupRequests.select{(DietGroupRequests.wantedUserId eq userId)}  .map { it[DietGroupRequests.groupId] }
        val requestIds=DietGroupRequests.select{(DietGroupRequests.wantedUserId eq userId)}.map { it[DietGroupRequests.id].value }
        val groupNames=mutableListOf<String>()
        val groupOwnerId=mutableListOf<Int>()
        val requestInfoList= mutableListOf<AddFriendRequestInfo>()

        for( i in groupIds) {
            val groupRecord = DietGroups.select { DietGroups.id eq i }.singleOrNull()
            if (groupRecord != null) {
                groupNames.add(groupRecord[DietGroups.groupName])
                groupOwnerId.add(groupRecord[DietGroups.groupOwnerId])

            } else {
                // Handle the case where no group is found for the given ID 'i'
                println("No group found with ID $i")
            }
        }
        for (i in groupIds.indices){
            requestInfoList.add(AddFriendRequestInfo(
               groupOwnerId =  groupOwnerId[i],
               groupName= groupNames[i],
               requestId =  requestIds[i]
            ))
        }

        requestInfoList


    }

    override suspend fun hasAlreadyInvited(wantedUserId: Int, dietGroupId: Int):Boolean= dbQuery {
        val c=DietGroupRequests.select {
            (DietGroupRequests.wantedUserId eq wantedUserId) and (DietGroupRequests.groupId eq dietGroupId)
        }.count()
        println("count $c")
        c>0
    }

}
