package com.thavin.email_invitations.data.remote.fake

import com.thavin.email_invitations.data.remote.model.Result
import com.thavin.email_invitations.data.remote.model.UserInfo
import com.thavin.email_invitations.data.remote.repository.RequestInviteRepository

class FakeRequestInviteRepositoryImpl : RequestInviteRepository {

    private var shouldReturnError = false

    override suspend fun requestInvite(userInfo: UserInfo): Result {
        return if (shouldReturnError) {
            Result.Error(message = "error")
        } else {
            Result.Success()
        }
    }

    fun shouldReturnError(returnError: Boolean) {
        shouldReturnError = returnError
    }
}