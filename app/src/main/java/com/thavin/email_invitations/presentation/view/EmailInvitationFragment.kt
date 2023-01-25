package com.thavin.email_invitations.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thavin.email_invitations.databinding.FragmentEmailInvitationBinding
import com.thavin.email_invitations.presentation.viewmodel.EmailInvitationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailInvitationFragment : Fragment() {

    private val viewModel: EmailInvitationViewModel by viewModels()

    private var _binding: FragmentEmailInvitationBinding? = null
    private val binding get() = _binding!!

    private val userDetailsDialogFragment = UserDetailsDialogFragment(testfun = {name, email -> viewModel.onRequestInviteClicked(name, email)})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailInvitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        setupUserDetailsDialogListeners()
        collectUiEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect {
                    when (it) {
                        is EmailInvitationViewModel.UiEvent.RequestInviteOnClick -> showUserDetailsDialogFragment()
                        is EmailInvitationViewModel.UiEvent.SendUserDetailsOnClick -> viewModel.onRequestInviteClicked(
                            it.name,
                            it.email
                        )
                        is EmailInvitationViewModel.UiEvent.ValidateName -> setValidateNameVisibility(
                            it.name
                        )
                        is EmailInvitationViewModel.UiEvent.ValidateEmail -> setValidateEmailVisibility(
                            it.email
                        )
                        is EmailInvitationViewModel.UiEvent.ValidateConfirmEmail -> setValidateConfirmEmailVisibility(
                            it.confirmEmail
                        )
                        is EmailInvitationViewModel.UiEvent.Loading -> userDetailsDialogFragment.showLoadingProgressBar()
                        is EmailInvitationViewModel.UiEvent.Complete -> userDetailsDialogFragment.showSuccess()
                        is EmailInvitationViewModel.UiEvent.DismissDialogOnClick -> userDetailsDialogFragment.dismissDialog()
                        else -> {}
                    }
                }
            }
        }

    private fun setupBinding() =
        with(binding) {
            buttonRequestUserDetails.setOnClickListener {
                viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.RequestInviteOnClick)
            }

            buttonCancelInvite.setOnClickListener {
                viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.CancelInviteOnClick)
            }
        }


    private fun setValidateNameVisibility(name: String) {
        if (viewModel.validateName(name)) {
            userDetailsDialogFragment.setValidateNameHintVisibility(INVISIBLE)
        } else {
            userDetailsDialogFragment.setValidateNameHintVisibility(VISIBLE)
        }
    }

    private fun setValidateEmailVisibility(email: String) {
        if (viewModel.validateEmail(email)) {
            userDetailsDialogFragment.setValidateEmailHintVisibility(INVISIBLE)
        } else {
            userDetailsDialogFragment.setValidateEmailHintVisibility(VISIBLE)
        }
    }

    private fun setValidateConfirmEmailVisibility(confirmEmail: String) {
        if (viewModel.validateConfirmEmail(confirmEmail)) {
            userDetailsDialogFragment.setValidateConfirmEmailHintVisibility(INVISIBLE)
        } else {
            userDetailsDialogFragment.setValidateConfirmEmailHintVisibility(VISIBLE)
        }
    }

    private fun setupUserDetailsDialogListeners() {
        userDetailsDialogFragment.setSendUserDetailsListener { name, email ->
            viewModel.sendUiEvent(
                EmailInvitationViewModel.UiEvent.SendUserDetailsOnClick(
                    name,
                    email
                )
            )
        }

        userDetailsDialogFragment.setNameValidationListener { name ->
            viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.ValidateName(name))
        }

        userDetailsDialogFragment.setEmailValidationListener { email ->
            viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.ValidateEmail(email))
        }

        userDetailsDialogFragment.setConfirmEmailValidationListener { confirmEmail ->
            viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.ValidateConfirmEmail(confirmEmail))
        }

        userDetailsDialogFragment.setSuccessDoneButtonListener {
            viewModel.sendUiEvent(EmailInvitationViewModel.UiEvent.DismissDialogOnClick)
        }
    }

    private fun showUserDetailsDialogFragment() =
        if (userDetailsDialogFragment.dialog?.isShowing == true) {
            Unit
        } else {
            userDetailsDialogFragment.show(
                childFragmentManager,
                UserDetailsDialogFragment.USER_DETAILS_TAG
            )
        }

    private fun showCancelInviteDialog() {

    }
}