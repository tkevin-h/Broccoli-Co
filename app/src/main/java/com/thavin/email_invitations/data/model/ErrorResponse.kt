package com.thavin.email_invitations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val errorMessage: String)
