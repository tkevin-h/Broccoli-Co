package com.thavin.email_invitations.data.local.fake

import com.thavin.email_invitations.data.local.repository.InviteStatusRepository

class FakeInviteStatusRepositoryImpl : InviteStatusRepository {

    private var shouldReturnInvited = false

    override suspend fun setInviteStatus(status: Boolean) {}

    override suspend fun getInviteStatus(): Boolean {
        return shouldReturnInvited
    }

    fun setInvitedStatus(status: Boolean) {
        shouldReturnInvited = status
    }
}