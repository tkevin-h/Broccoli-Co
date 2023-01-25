package com.thavin.email_invitations.data

import com.thavin.email_invitations.data.model.ErrorResponse
import com.thavin.email_invitations.data.model.Result
import com.thavin.email_invitations.data.model.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EmailInvitationRepositoryImpl(
    private val emailInvitationApi: EmailInvitationApi
) : EmailInvitationRepository {

    override suspend fun sendInvitation(userInfo: UserInfo): Result =
        withContext(Dispatchers.IO) {
            try {
                val result = emailInvitationApi.postUserInfo(userInfo)
                result.errorBody()?.let {
                    val errorMessage = getErrorMessage(it.string())
                    Result.Error(message = errorMessage)
                } ?: run {
                    if (result.body() != null) {
                        Result.Success(result.body().toString())
                    } else {
                        Result.Success()
                    }
                }
            } catch (e: Exception) {
                Result.Error(exception = e)
            }
        }

    private fun getErrorMessage(error: String): String {
        val errorResponse = Json.decodeFromString<ErrorResponse>(error)
        return errorResponse.errorMessage
    }
}