package com.thavin.email_invitations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thavin.email_invitations.data.EmailInvitationRepository
import com.thavin.email_invitations.data.Result
import com.thavin.email_invitations.data.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailInvitationViewModel @Inject constructor(
    private val emailInvitationRepository: EmailInvitationRepository
) : ViewModel() {

    fun onRequestInviteClicked(name: String, email: String) {
        MainScope().launch {
            when(val result = emailInvitationRepository.sendInvitation(UserInfo(name = name, email = email))) {
                is Result.Success -> {}
                is Result.Error -> println(result.exception)
            }
        }
    }
}