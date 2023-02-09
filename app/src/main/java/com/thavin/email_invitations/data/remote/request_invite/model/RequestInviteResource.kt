package com.thavin.email_invitations.data.remote.request_invite.model

sealed class RequestInviteResource {
    data class Success(val message: String? = null) : RequestInviteResource()
    object Loading : RequestInviteResource()
    data class Error(val exception: Exception? = null, val message: String? = null) : RequestInviteResource()
}