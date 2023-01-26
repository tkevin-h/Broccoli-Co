package com.thavin.email_invitations.data.local.repository

import kotlinx.coroutines.flow.Flow

interface InviteStatusRepository {

    suspend fun setInviteStatus(status: Boolean)

    suspend fun getInviteStatus(): Flow<Boolean>
}