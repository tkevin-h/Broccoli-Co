package com.thavin.email_invitations.data.remote.repository

import com.thavin.email_invitations.data.remote.model.Result
import com.thavin.email_invitations.data.remote.model.UserInfo

interface RequestInviteRepository {

    suspend fun requestInvite(userInfo: UserInfo): Result
}