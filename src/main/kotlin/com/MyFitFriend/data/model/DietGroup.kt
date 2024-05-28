package com.MyFitFriend.data.model

import com.MyFitFriend.data.model.DietGroupMembers.references
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Serializable
data class DietGroup(
    val groupName: String,
    val groupOwnerId:Int,
    val groupId:Int?
)

object DietGroups : IntIdTable()  {
    val groupName = varchar("groupName", 255)
    val groupOwnerId=integer("groupOwnerId")

}

// For the many-to-many relationship between users and groups
object DietGroupMembers : Table() {
        val dietGroupId = integer("dietGroupId").references(DietGroups.id)
    val groupMemberId=integer("groupMemberId")
}
object DietGroupRequests : IntIdTable()  {
    val groupId = integer("groupId").references(DietGroups.id)
    val wantedUserId=integer("wantedUserId")
}

