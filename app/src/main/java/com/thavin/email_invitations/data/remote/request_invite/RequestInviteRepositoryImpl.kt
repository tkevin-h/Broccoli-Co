package com.thavin.email_invitations.data.remote.request_invite

import com.thavin.email_invitations.data.remote.request_invite.dto.ErrorResponse
import com.thavin.email_invitations.data.remote.request_invite.model.RequestInviteResource
import com.thavin.email_invitations.data.remote.request_invite.dto.UserInfo
import com.thavin.email_invitations.data.remote.request_invite.repository.RequestInviteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RequestInviteRepositoryImpl(
    private val requestInviteApi: RequestInviteApi
) : RequestInviteRepository {

    override suspend fun requestInvite(userInfo: UserInfo): RequestInviteResource =
        withContext(Dispatchers.IO) {
            try {
                val result = requestInviteApi.postUserInfo(userInfo)
                result.errorBody()?.let {
                    val errorMessage = getErrorMessage(it.string())
                    RequestInviteResource.Error(message = errorMessage)
                } ?: run {
                    if (result.body() != null) {
                        RequestInviteResource.Success(message = result.body().toString())
                    } else {
                        RequestInviteResource.Success()
                    }
                }
            } catch (e: Exception) {
                RequestInviteResource.Error(exception = e)
            }
        }

    private fun getErrorMessage(error: String): String {
        val errorResponse = Json.decodeFromString<ErrorResponse>(error)
        return errorResponse.errorMessage
    }
}