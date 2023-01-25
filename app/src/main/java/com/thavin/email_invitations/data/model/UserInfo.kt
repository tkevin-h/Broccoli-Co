package com.thavin.email_invitations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String,
    val email: String
)
