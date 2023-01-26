package com.thavin.email_invitations.data.remote.model

sealed class Result {
    data class Success(val message: String? = null) : Result()
    data class Error(val exception: Exception? = null, val message: String? = null) : Result()
}