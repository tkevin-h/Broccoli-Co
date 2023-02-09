package com.thavin.email_invitations.data.remote.fake

import com.thavin.email_invitations.data.remote.request_invite.model.RequestInviteResource
import com.thavin.email_invitations.data.remote.request_invite.dto.UserInfo
import com.thavin.email_invitations.data.remote.request_invite.repository.RequestInviteRepository

class FakeRequestInviteRepositoryImpl : RequestInviteRepository {

    private var shouldReturnError = false

    override suspend fun requestInvite(userInfo: UserInfo): RequestInviteResource {
        return if (shouldReturnError) {
            RequestInviteResource.Error(message = "error")
        } else {
            RequestInviteResource.Success()
        }
    }

    fun shouldReturnError(returnError: Boolean) {
        shouldReturnError = returnError
    }
}