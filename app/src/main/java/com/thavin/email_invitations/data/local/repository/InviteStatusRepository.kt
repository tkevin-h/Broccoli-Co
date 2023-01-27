package com.thavin.email_invitations.data.local.repository

interface InviteStatusRepository {

    suspend fun setInviteStatus(status: Boolean)

    suspend fun getInviteStatus(): Boolean
}