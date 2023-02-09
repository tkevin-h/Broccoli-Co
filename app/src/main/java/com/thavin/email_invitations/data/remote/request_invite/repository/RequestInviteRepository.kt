package com.thavin.email_invitations.data.remote.request_invite.repository

import com.thavin.email_invitations.data.remote.request_invite.model.RequestInviteResource
import com.thavin.email_invitations.data.remote.request_invite.dto.UserInfo

interface RequestInviteRepository {

    suspend fun requestInvite(userInfo: UserInfo): RequestInviteResource
}