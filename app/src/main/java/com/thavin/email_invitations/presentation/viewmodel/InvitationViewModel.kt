package com.thavin.email_invitations.presentation.viewmodel

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thavin.email_invitations.data.local.repository.InviteStatusRepository
import com.thavin.email_invitations.data.remote.request_invite.repository.RequestInviteRepository
import com.thavin.email_invitations.data.remote.request_invite.model.RequestInviteResource
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFactsResource
import com.thavin.email_invitations.data.remote.cat_facts.repository.CatFactsRepository
import com.thavin.email_invitations.data.remote.request_invite.dto.UserInfo
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val requestInviteRepository: RequestInviteRepository,
    private val inviteStatusRepository: InviteStatusRepository,
    private val catFactsRepository: CatFactsRepository
) : ViewModel() {

    private val _invitationUiEvent = Channel<InvitationUiEvent>()
    val invitationUiEvent = _invitationUiEvent.receiveAsFlow()

    private val _catFactsUiEvent = Channel<CatFactsUiEvent>()
    val astroUiEvent = _catFactsUiEvent.receiveAsFlow()

    private val _userDetailsUiEvent = Channel<UserDetailsUiEvent>()
    val userDetailsUiEvent = _userDetailsUiEvent.receiveAsFlow()

    private val _cancelInviteUiEvent = Channel<CancelInviteUiEvent>()
    val cancelInviteUiEvent = _cancelInviteUiEvent.receiveAsFlow()

    sealed class InvitationUiEvent {
        object RequestInviteOnClick : InvitationUiEvent()

        object ShowPostInviteScreen : InvitationUiEvent()

        object ShowPreInviteScreen : InvitationUiEvent()

        object RequestCancelInviteOnClick : InvitationUiEvent()
    }

    sealed class CatFactsUiEvent {
        object ShowLoading : CatFactsUiEvent()
        data class ShowCatFactsList(val facts: MutableList<CatFacts>?) : CatFactsUiEvent()
        object ShowError : CatFactsUiEvent()
    }

    sealed class UserDetailsUiEvent {
        object InvalidName : UserDetailsUiEvent()
        object ValidName : UserDetailsUiEvent()

        object InvalidEmail : UserDetailsUiEvent()

        object ValidEmail : UserDetailsUiEvent()

        object InvalidConfirmEmail : UserDetailsUiEvent()

        object ValidConfirmEmail : UserDetailsUiEvent()

        object InviteDetailsLoading : UserDetailsUiEvent()

        object InviteDetailsSuccess : UserDetailsUiEvent()

        data class InviteDetailsError(val message: String?) : UserDetailsUiEvent()

        object DismissInviteDetailsDialogOnClick : UserDetailsUiEvent()
    }

    sealed class CancelInviteUiEvent {

        object CancelInviteSuccess : CancelInviteUiEvent()

        object DismissCancelInviteDialogOnClick : CancelInviteUiEvent()
    }

    // Public Functions
    fun requestInviteOnClick() {
        sendInvitationUiEvent(InvitationUiEvent.RequestInviteOnClick)
    }

    fun requestCancelInviteOnClick() {
        sendInvitationUiEvent(InvitationUiEvent.RequestCancelInviteOnClick)
    }

    fun sendUserDetailsOnClick(name: String, email: String, confirmEmail: String) {
        if (validateFields(name, email, confirmEmail)) {
            sendUserDetailsUiEvent(UserDetailsUiEvent.InviteDetailsLoading)
            viewModelScope.launch {
                when (val result = requestInviteRepository.requestInvite(
                    UserInfo(name = name, email = email)
                )) {
                    is RequestInviteResource.Success -> {
                        inviteStatusRepository.setInviteStatus(true)
                        sendUserDetailsUiEvent(UserDetailsUiEvent.InviteDetailsSuccess)
                    }
                    is RequestInviteResource.Error -> sendUserDetailsUiEvent(
                        UserDetailsUiEvent.InviteDetailsError(
                            result.message
                        )
                    )
                    else -> {}
                }
            }
        }
    }

    fun dismissInviteDetailsDialogOnClick() {
        sendUserDetailsUiEvent(UserDetailsUiEvent.DismissInviteDetailsDialogOnClick)
    }

    fun checkInviteStatus() =
        viewModelScope.launch {
            if (inviteStatusRepository.getInviteStatus()) {
                sendInvitationUiEvent(InvitationUiEvent.ShowPostInviteScreen)
            } else {
                sendInvitationUiEvent(InvitationUiEvent.ShowPreInviteScreen)
            }
        }

    fun cancelInviteOnClick() =
        viewModelScope.launch {
            inviteStatusRepository.setInviteStatus(false)
            sendCancelUiEvent(CancelInviteUiEvent.CancelInviteSuccess)
        }

    fun dismissCancelInviteDialogOnClick() {
        sendCancelUiEvent(CancelInviteUiEvent.DismissCancelInviteDialogOnClick)
    }

    fun getAstros() {
        viewModelScope.launch {
            catFactsRepository.getCatFacts().collect {
                when (it) {
                    is CatFactsResource.Loading -> _catFactsUiEvent.send(CatFactsUiEvent.ShowLoading)
                    is CatFactsResource.Success -> _catFactsUiEvent.send(
                        CatFactsUiEvent.ShowCatFactsList(
                            it.facts
                        )
                    )
                    is CatFactsResource.Error -> _catFactsUiEvent.send(CatFactsUiEvent.ShowError)
                }
            }
        }
    }

    // Private Functions
    private fun sendInvitationUiEvent(event: InvitationUiEvent) =
        viewModelScope.launch {
            _invitationUiEvent.send(event)
        }

    private fun sendUserDetailsUiEvent(event: UserDetailsUiEvent) =
        viewModelScope.launch {
            _userDetailsUiEvent.send(event)
        }

    private fun sendCancelUiEvent(event: CancelInviteUiEvent) =
        viewModelScope.launch {
            _cancelInviteUiEvent.send(event)
        }

    private fun validateFields(name: String, email: String, confirmEmail: String): Boolean {
        val isNameValid = validateName(name)
        val isEmailValid = validateEmail(email)
        val isConfirmEmailValid = validateConfirmEmail(confirmEmail, email)

        if (isNameValid) {
            sendUserDetailsUiEvent(UserDetailsUiEvent.ValidName)
        } else {
            sendUserDetailsUiEvent(UserDetailsUiEvent.InvalidName)
        }

        if (isEmailValid) {
            sendUserDetailsUiEvent(UserDetailsUiEvent.ValidEmail)
        } else {
            sendUserDetailsUiEvent(UserDetailsUiEvent.InvalidEmail)
        }

        if (isConfirmEmailValid) {
            sendUserDetailsUiEvent(UserDetailsUiEvent.ValidConfirmEmail)
        } else {
            sendUserDetailsUiEvent(UserDetailsUiEvent.InvalidConfirmEmail)
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