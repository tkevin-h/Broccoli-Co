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
import com.thavin.email_invitations.databinding.FragmentInvitationBinding
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.UiEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvitationFragment : Fragment() {

    private val viewModel: InvitationViewModel by viewModels()

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

        setupBinding()
        setupInviteDetailsDialog()
        setupCancelInviteDialog()
        collectUiEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Private Functions
    private fun collectUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect {
                    when (it) {
                        is RequestInviteOnClick -> showInviteDetailsDialog()
                        is InvalidName -> inviteDetailsDialog?.setValidateNameHintVisibility(VISIBLE)
                        is ValidName -> inviteDetailsDialog?.setValidateNameHintVisibility(INVISIBLE)
                        is InvalidEmail -> inviteDetailsDialog?.setValidateEmailHintVisibility(
                            VISIBLE
                        )
                        is ValidEmail -> inviteDetailsDialog?.setValidateEmailHintVisibility(
                            INVISIBLE
                        )
                        is InvalidConfirmEmail -> inviteDetailsDialog?.setValidateConfirmEmailHintVisibility(
                            VISIBLE
                        )
                        is ValidConfirmEmail -> inviteDetailsDialog?.setValidateConfirmEmailHintVisibility(
                            INVISIBLE
                        )
                        is InviteDetailsLoading -> inviteDetailsDialog?.showLoading()
                        is InviteDetailsSuccess -> inviteDetailsDialog?.showSuccessDialog()
                        is InviteDetailsError -> inviteDetailsDialog?.showError(it.message)
                        is DismissInviteDetailsDialogOnClick -> inviteDetailsDialog?.dismissDialog()
                        is ShowPostInviteScreen -> showPostInviteScreen()
                        is ShowPreInviteScreen -> showPreInviteScreen()
                        is CancelInviteOnClick -> showCancelInviteDialog()
                        is CancelInviteSuccess -> cancelInviteDialog?.showSuccess()
                        is DismissCancelInviteDialogOnClick -> cancelInviteDialog?.dismissDialog()
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