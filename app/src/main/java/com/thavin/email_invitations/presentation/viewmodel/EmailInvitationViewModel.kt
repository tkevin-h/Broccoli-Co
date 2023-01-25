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

        data class SendUserDetailsOnClick(val name: String, val email: String) : UiEvent()

        object Loading : UiEvent()

        object Complete : UiEvent()

        object InvalidName : UiEvent()
        object ValidName : UiEvent()
        data class ValidateName(val name: String) : UiEvent()

        data class ValidateEmail(val email: String) : UiEvent()

        data class ValidateConfirmEmail(val confirmEmail: String) : UiEvent()

        object DismissDialogOnClick : UiEvent()

        object CancelInviteOnClick : UiEvent()
    }

    private fun sendUiEvent(event: UiEvent) =
        viewModelScope.launch {
            _uiEvent.send(event)
        }

    fun requestInvitationOnClick() {
        sendUiEvent(UiEvent.RequestInviteOnClick)
    }

    fun cancelInvitationOnClick() {
        sendUiEvent(UiEvent.CancelInviteOnClick)
    }

    fun sendUserDetailsOnClick(name: String, email: String) {
        if (isNameValid && isEmailValid && isConfirmEmailValid) {
            sendUiEvent(UiEvent.Loading)
            viewModelScope.launch {
                when (val result = emailInvitationRepository.sendInvitation(
                    UserInfo(
                        name = name,
                        email = email
                    )
                )) {
                    is Result.Success -> sendUiEvent(UiEvent.Complete)
                    is Result.Error -> println(result.exception)
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

    fun validateEmail(email: String): Boolean {
        currentEmail = email

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false
            return false
        }

        isEmailValid = true
        return true
    }

    fun validateConfirmEmail(email: String): Boolean {
        if (email != currentEmail) {
            isConfirmEmailValid = false
            return false
        }

        isConfirmEmailValid = true
        return true
    }

    fun dismissDialogOnClick() {
        sendUiEvent(UiEvent.DismissDialogOnClick)
    }
}