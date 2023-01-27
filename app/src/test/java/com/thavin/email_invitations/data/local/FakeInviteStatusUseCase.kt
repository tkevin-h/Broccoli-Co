package com.thavin.email_invitations.data.local

import com.thavin.email_invitations.data.local.repository.InviteStatusRepository

class FakeInviteStatusUseCase : InviteStatusRepository {

    private var shouldReturnInvited = false

    override suspend fun setInviteStatus(status: Boolean) {}

    override suspend fun getInviteStatus(): Boolean {
        return shouldReturnInvited
    }

    fun setInvitedStatus(status: Boolean) {
        shouldReturnInvited = status
    }
}