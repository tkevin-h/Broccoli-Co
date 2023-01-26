package com.thavin.email_invitations.data

import com.thavin.email_invitations.data.model.Result
import com.thavin.email_invitations.data.model.UserInfo

interface InvitationRepository {

    suspend fun sendInvitation(userInfo: UserInfo): Result
}