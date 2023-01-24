package com.thavin.email_invitations.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

//data class UserInfo(
//    @SerializedName("name") val name: String,
//    @SerializedName("email") val email: String
//)

@Serializable
data class UserInfo(
    val name: String,
    val email: String
)
