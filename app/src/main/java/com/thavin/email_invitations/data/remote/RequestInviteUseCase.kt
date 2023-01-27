package com.thavin.email_invitations.data.remote

import com.thavin.email_invitations.data.remote.model.ErrorResponse
import com.thavin.email_invitations.data.remote.model.Result
import com.thavin.email_invitations.data.remote.model.UserInfo
import com.thavin.email_invitations.data.remote.repository.RequestInviteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RequestInviteUseCase(
    private val requestInviteApi: RequestInviteApi
) : RequestInviteRepository {

    override suspend fun requestInvite(userInfo: UserInfo): Result =
        withContext(Dispatchers.IO) {
            try {
                val result = requestInviteApi.postUserInfo(userInfo)
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