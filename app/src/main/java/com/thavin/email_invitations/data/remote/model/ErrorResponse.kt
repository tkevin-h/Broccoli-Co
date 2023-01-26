package com.thavin.email_invitations.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val errorMessage: String)
