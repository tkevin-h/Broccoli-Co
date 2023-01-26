package com.thavin.email_invitations.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thavin.email_invitations.data.EmailInvitationRepository
import com.thavin.email_invitations.data.model.Result
import com.thavin.email_invitations.data.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailInvitationViewModel @Inject constructor(
    private val emailInvitationRepository: EmailInvitationRepository
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

        object SendUserDetailsLoading : UiEvent()

        object SendUserDetailsComplete : UiEvent()

        data class SendUserDetailsError(val message: String?) : UiEvent()

        object DismissDialogOnClick : UiEvent()

        object CancelInviteOnClick : UiEvent()
    }

    private fun sendUiEvent(event: UiEvent) =
        viewModelScope.launch {
            _uiEvent.send(event)
        }

    fun requestInviteOnClick() {
        sendUiEvent(UiEvent.RequestInviteOnClick)
    }

    fun cancelInviteOnClick() {
        sendUiEvent(UiEvent.CancelInviteOnClick)
    }

    fun sendUserDetailsOnClick(name: String, email: String) {
        if (isNameValid && isEmailValid && isConfirmEmailValid) {
            sendUiEvent(UiEvent.SendUserDetailsLoading)
            viewModelScope.launch {
                when (val result = emailInvitationRepository.sendInvitation(
                    UserInfo(
                        name = name,
                        email = email
                    )
                )) {
                    is Result.Success -> sendUiEvent(UiEvent.SendUserDetailsComplete)
                    is Result.Error -> sendUiEvent(UiEvent.SendUserDetailsError(result.message))
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

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    fun dismissDialogOnClick() {
        sendUiEvent(UiEvent.DismissDialogOnClick)
    }
}