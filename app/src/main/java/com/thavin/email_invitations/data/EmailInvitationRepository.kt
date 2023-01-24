package com.thavin.email_invitations.data

interface EmailInvitationRepository {

    suspend fun sendInvitation(userInfo: UserInfo): Result
}