package com.thavin.email_invitations.presentation.viewmodel

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thavin.email_invitations.data.local.repository.InviteStatusRepository
import com.thavin.email_invitations.data.remote.repository.RequestInviteRepository
import com.thavin.email_invitations.data.remote.model.Result
import com.thavin.email_invitations.data.remote.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val requestInviteRepository: RequestInviteRepository,
    private val inviteStatusRepository: InviteStatusRepository
) : ViewModel() {

    private var isNameValid = false
    private var isEmailValid = false
    private var isConfirmEmailValid = false
    private lateinit var currentEmail: String

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        object RequestInviteOnClick : UiEvent()

        object InvalidName : UiEvent()
        object ValidName : UiEvent()

        object InvalidEmail : UiEvent()

        object ValidEmail : UiEvent()

        object InvalidConfirmEmail : UiEvent()

        object ValidConfirmEmail : UiEvent()

        object InviteDetailsLoading : UiEvent()

        object InviteDetailsSuccess : UiEvent()

        data class InviteDetailsError(val message: String?) : UiEvent()

        object DismissInviteDetailsDialogOnClick : UiEvent()

        object ShowPostInviteScreen : UiEvent()

        object ShowPreInviteScreen : UiEvent()

        object CancelInviteOnClick : UiEvent()

        object CancelInviteSuccess : UiEvent()

        object DismissCancelInviteDialogOnClick : UiEvent()
    }

    // Public Functions
    fun requestInviteOnClick() {
        sendUiEvent(UiEvent.RequestInviteOnClick)
    }

    fun requestCancelInviteOnClick() {
        sendUiEvent(UiEvent.CancelInviteOnClick)
    }

    fun sendUserDetailsOnClick(name: String, email: String) {
        if (isNameValid && isEmailValid && isConfirmEmailValid) {
            sendUiEvent(UiEvent.InviteDetailsLoading)
            viewModelScope.launch {
                when (val result = requestInviteRepository.requestInvite(
                    UserInfo(name = name, email = email)
                )) {
                    is Result.Success -> {
                        inviteStatusRepository.setInviteStatus(true)
                        sendUiEvent(UiEvent.InviteDetailsSuccess)
                    }
                    is Result.Error -> sendUiEvent(UiEvent.InviteDetailsError(result.message))
                }
            }
        }
    }

    fun validateName(name: String) {
        if (name.length <= 3) {
            isNameValid = false
            sendUiEvent(UiEvent.InvalidName)
        } else {
            isNameValid = true
            sendUiEvent(UiEvent.ValidName)
        }
    }

    fun validateEmail(email: String) {
        currentEmail = email

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false
            sendUiEvent(UiEvent.InvalidEmail)
        } else {
            isEmailValid = true
            sendUiEvent(UiEvent.ValidEmail)
        }
    }

    fun validateConfirmEmail(email: String) {
        if (email != currentEmail) {
            isConfirmEmailValid = false
            sendUiEvent(UiEvent.InvalidConfirmEmail)
        } else {
            isConfirmEmailValid = true
            sendUiEvent(UiEvent.ValidConfirmEmail)
        }
    }

    fun dismissInviteDetailsDialogOnClick() {
        sendUiEvent(UiEvent.DismissInviteDetailsDialogOnClick)
    }

    fun checkInviteStatus() =
        viewModelScope.launch {
            if (inviteStatusRepository.getInviteStatus()) {
                sendUiEvent(UiEvent.ShowPostInviteScreen)
            } else {
                sendUiEvent(UiEvent.ShowPreInviteScreen)
            }
        }

    fun cancelInviteOnClick() =
        viewModelScope.launch {
            inviteStatusRepository.setInviteStatus(false)
            sendUiEvent(UiEvent.CancelInviteSuccess)
        }

    fun dismissCancelInviteDialogOnClick() {
        sendUiEvent(UiEvent.DismissCancelInviteDialogOnClick)
    }

    // Private Functions
    private fun sendUiEvent(event: UiEvent) =
        viewModelScope.launch {
            _uiEvent.send(event)
        }
}