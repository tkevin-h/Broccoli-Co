package com.thavin.email_invitations.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.thavin.email_invitations.R
import com.thavin.email_invitations.databinding.FragmentInvitationBinding
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.InvitationUiEvent.*
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.UserDetailsUiEvent.*
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.CancelInviteUiEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvitationFragment : Fragment() {

    private val viewModel: InvitationViewModel by activityViewModels()

    private var _binding: FragmentInvitationBinding? = null
    private val binding get() = _binding!!

    private var inviteDetailsDialog: InviteDetailsDialog? = null
    private var cancelInviteDialog: CancelInviteDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkInviteStatus()

        setupBinding()
        setupInviteDetailsDialog()
        setupCancelInviteDialog()
        collectInvitationUiEvents()
        collectUserDetailsUiEvents()
        collectCancelInviteUiEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Private Functions
    private fun collectInvitationUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.invitationUiEvent.collect {
                    when (it) {
                        is RequestInviteOnClick -> showInviteDetailsDialog()
                        is ShowPostInviteScreen -> showPostInviteScreen()
                        is ShowPreInviteScreen -> showPreInviteScreen()
                        is RequestCancelInviteOnClick -> showCancelInviteDialog()
                        else -> {}
                    }
                }
            }
        }

    private fun collectUserDetailsUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userDetailsUiEvent.collect {
                    inviteDetailsDialog?.run {
                        when (it) {
                            is InvalidName -> setValidateNameHintVisibility(VISIBLE)
                            is ValidName -> setValidateNameHintVisibility(INVISIBLE)
                            is InvalidEmail -> setValidateEmailHintVisibility(VISIBLE)
                            is ValidEmail -> setValidateEmailHintVisibility(INVISIBLE)
                            is InvalidConfirmEmail -> setValidateConfirmEmailHintVisibility(VISIBLE)
                            is ValidConfirmEmail -> setValidateConfirmEmailHintVisibility(INVISIBLE)
                            is InviteDetailsLoading -> showLoading()
                            is InviteDetailsSuccess -> showSuccessDialog()
                            is InviteDetailsError -> showError(it.message)
                            is DismissInviteDetailsDialogOnClick -> dismissDialog()
                        }
                    }
                }
            }
        }

    private fun collectCancelInviteUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cancelInviteUiEvent.collect {
                    cancelInviteDialog?.run {
                        when (it) {
                            is CancelInviteSuccess -> showSuccess()
                            is DismissCancelInviteDialogOnClick -> dismissDialog()
                        }
                    }
                }
            }
        }

    private fun setupBinding() =
        with(binding) {
            buttonRequestUserDetails.setOnClickListener {
                viewModel.requestInviteOnClick()
            }

            buttonCancelInvite.setOnClickListener {
                viewModel.requestCancelInviteOnClick()
            }

            buttonSettings.setOnClickListener {
                findNavController().navigate(R.id.action_EmailInvitationFragment_to_TestFragment)
            }
        }

    private fun setupInviteDetailsDialog() {
        inviteDetailsDialog = InviteDetailsDialog(
            sendUserDetails = { name, email, confirmEmail ->
                viewModel.sendUserDetailsOnClick(
                    name,
                    email,
                    confirmEmail
                )
            },
            done = { viewModel.dismissInviteDetailsDialogOnClick() },
            checkInviteStatus = { viewModel.checkInviteStatus() }
        )
    }

    private fun setupCancelInviteDialog() {
        cancelInviteDialog = CancelInviteDialog(
            cancelInvite = { viewModel.cancelInviteOnClick() },
            done = { viewModel.dismissCancelInviteDialogOnClick() },
            checkInviteStatus = { viewModel.checkInviteStatus() }
        )
    }

    private fun showInviteDetailsDialog() =
        if (inviteDetailsDialog?.dialog?.isShowing == true) {
            Unit
        } else {
            inviteDetailsDialog?.show(
                childFragmentManager,
                InviteDetailsDialog.INVITE_DETAILS_TAG
            )
        }

    private fun showCancelInviteDialog() =
        if (cancelInviteDialog?.dialog?.isShowing == true) {
            Unit
        } else {
            cancelInviteDialog?.show(
                childFragmentManager,
                CancelInviteDialog.CANCEL_INVITE_TAG
            )
        }

    private fun showPreInviteScreen() =
        with(binding) {
            linearlayoutInvited.visibility = GONE
            linearlayoutRequestInvite.visibility = VISIBLE
        }

    private fun showPostInviteScreen() =
        with(binding) {
            linearlayoutRequestInvite.visibility = GONE
            linearlayoutInvited.visibility = VISIBLE
        }
}