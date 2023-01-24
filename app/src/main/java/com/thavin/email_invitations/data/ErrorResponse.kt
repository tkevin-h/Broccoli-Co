package com.thavin.email_invitations.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(@SerialName("errorMessage") val errorMessage: String)
