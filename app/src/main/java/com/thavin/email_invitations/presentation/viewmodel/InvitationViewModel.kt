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

    fun sendUserDetailsOnClick(name: String, email: String, confirmEmail: String) {
        if (validateFields(name, email, confirmEmail)) {
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

    private fun validateFields(name: String, email: String, confirmEmail: String): Boolean {
        val isNameValid = validateName(name)
        val isEmailValid = validateEmail(email)
        val isConfirmEmailValid = validateConfirmEmail(confirmEmail, email)

        if (isNameValid) {
            sendUiEvent(UiEvent.ValidName)
        } else {
            sendUiEvent(UiEvent.InvalidName)
        }

        if (isEmailValid) {
            sendUiEvent(UiEvent.ValidEmail)
        } else {
            sendUiEvent(UiEvent.InvalidEmail)
        }

        if (isConfirmEmailValid) {
            sendUiEvent(UiEvent.ValidConfirmEmail)
        } else {
            sendUiEvent(UiEvent.InvalidConfirmEmail)
        }

        return isNameValid && isEmailValid && isConfirmEmailValid
    }

    private fun validateName(name: String): Boolean =
        name.length > 3


    private fun validateEmail(email: String): Boolean =
        PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()


    private fun validateConfirmEmail(confirmEmail: String, currentEmail: String): Boolean =
        confirmEmail == currentEmail
}