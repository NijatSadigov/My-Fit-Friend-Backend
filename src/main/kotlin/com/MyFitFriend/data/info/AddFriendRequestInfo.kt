package com.MyFitFriend.data.info

import kotlinx.serialization.Serializable

@Serializable
data class AddFriendRequestInfo(
    val groupOwnerId:Int,
    val groupName:String,
    val requestId:Int

)
