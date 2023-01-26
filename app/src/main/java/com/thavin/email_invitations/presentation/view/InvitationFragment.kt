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
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.UiEvent.*
import com.thavin.email_invitations.presentation.widget.InviteDetailsDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvitationFragment : Fragment() {

    private val viewModel: InvitationViewModel by viewModels()

    private var _binding: FragmentEmailInvitationBinding? = null
    private val binding get() = _binding!!

    private var inviteDetailsDialogFragment: InviteDetailsDialogFragment? = null

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
        setupUserDetailsDialogFragment()
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
                    inviteDetailsDialogFragment?.run {
                        when (it) {
                            is RequestInviteOnClick -> showUserDetailsDialogFragment()
                            is InvalidName -> setValidateNameHintVisibility(VISIBLE)
                            is ValidName -> setValidateNameHintVisibility(INVISIBLE)
                            is InvalidEmail -> setValidateEmailHintVisibility(VISIBLE)
                            is ValidEmail -> setValidateEmailHintVisibility(INVISIBLE)
                            is InvalidConfirmEmail -> setValidateConfirmEmailHintVisibility(VISIBLE)
                            is ValidConfirmEmail -> setValidateConfirmEmailHintVisibility(INVISIBLE)
                            is SendUserDetailsLoading -> showLoadingProgressBar()
                            is SendUserDetailsSuccess -> showSuccessDialog()
                            is SendUserDetailsError -> showError(it.message)
                            is DismissDialogOnClick -> dismissDialog()
                            is ShowPostInviteScreen -> showPostInviteScreen()
                            is ShowPreInviteScreen -> showPreInviteScreen()
                            is CancelInviteOnClick -> {}
                        }
                    }
//                    when (it) {
//                        is RequestInviteOnClick -> showUserDetailsDialogFragment()
//                        is InvalidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(VISIBLE)
//                        is ValidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(INVISIBLE)
//                        is InvalidEmail -> userDetailsDialogFragment?.setValidateEmailHintVisibility(VISIBLE)
//                        is ValidEmail -> userDetailsDialogFragment?.setValidateEmailHintVisibility(INVISIBLE)
//                        is InvalidConfirmEmail -> userDetailsDialogFragment?.setValidateConfirmEmailHintVisibility(VISIBLE)
//                        is ValidConfirmEmail -> userDetailsDialogFragment?.setValidateConfirmEmailHintVisibility(INVISIBLE)
//                        is SendUserDetailsLoading -> userDetailsDialogFragment?.showLoadingProgressBar()
//                        is SendUserDetailsSuccess -> userDetailsDialogFragment?.showSuccessDialog()
//                        is SendUserDetailsError -> userDetailsDialogFragment?.showError(it.message)
//                        is DismissDialogOnClick -> userDetailsDialogFragment?.dismissDialog()
//                        is ShowPostInviteScreen -> userDetailsDialogFragment?.showPostInviteScreen()
//                        is ShowPreInviteScreen -> userDetailsDialogFragment?.showPreInviteScreen()
//                        is CancelInviteOnClick -> {}
//                    }
                }
            }
        }

    private fun setupBinding() =
        with(binding) {
            buttonRequestUserDetails.setOnClickListener {
                viewModel.requestInviteOnClick()
            }

            buttonCancelInvite.setOnClickListener {
                viewModel.cancelInviteOnClick()
            }
        }

    private fun setupUserDetailsDialogFragment() {
        inviteDetailsDialogFragment = InviteDetailsDialogFragment(
            sendUserDetails = { name, email -> viewModel.sendUserDetailsOnClick(name, email) },
            validateName = { name -> viewModel.validateName(name) },
            validateEmail = { email -> viewModel.validateEmail(email) },
            validateConfirmEmail = { confirmEmail -> viewModel.validateConfirmEmail(confirmEmail) },
            done = { viewModel.dismissDialogOnClick() },
            checkInviteStatus = { viewModel.checkInviteStatus() }
        )
    }

    private fun showUserDetailsDialogFragment() =
        if (inviteDetailsDialogFragment?.dialog?.isShowing == true) {
            Unit
        } else {
            inviteDetailsDialogFragment?.show(
                childFragmentManager,
                InviteDetailsDialogFragment.USER_DETAILS_TAG
            )
        }

    private fun showCancelInviteDialog() {

    }

    private fun showPreInviteScreen() =
        with (binding) {
            linearlayoutInvited.visibility = GONE
            linearlayoutRequestInvite.visibility = VISIBLE
        }

    private fun showPostInviteScreen() =
        with (binding) {
            linearlayoutRequestInvite.visibility = GONE
            linearlayoutInvited.visibility = VISIBLE
        }
}