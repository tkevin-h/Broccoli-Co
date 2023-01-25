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

    private var userDetailsDialogFragment: UserDetailsDialogFragment? = null

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
        setupUserDetailsDialog()
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
                        is EmailInvitationViewModel.UiEvent.SendUserDetailsOnClick -> viewModel.sendUserDetailsOnClick(
                            it.name,
                            it.email
                        )
                        is EmailInvitationViewModel.UiEvent.InvalidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(VISIBLE)
                        is EmailInvitationViewModel.UiEvent.ValidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(INVISIBLE)
                        is EmailInvitationViewModel.UiEvent.ValidateEmail -> setValidateEmailVisibility(
                            it.email
                        )
                        is EmailInvitationViewModel.UiEvent.ValidateConfirmEmail -> setValidateConfirmEmailVisibility(
                            it.confirmEmail
                        )
                        is EmailInvitationViewModel.UiEvent.Loading -> userDetailsDialogFragment?.showLoadingProgressBar()
                        is EmailInvitationViewModel.UiEvent.Complete -> userDetailsDialogFragment?.showSuccess()
                        is EmailInvitationViewModel.UiEvent.DismissDialogOnClick -> userDetailsDialogFragment?.dismissDialog()
                        else -> {}
                    }
                }
            }
        }

    private fun setupBinding() =
        with(binding) {
            buttonRequestUserDetails.setOnClickListener {
                viewModel.requestInvitationOnClick()
            }

            buttonCancelInvite.setOnClickListener {
                viewModel.cancelInvitationOnClick()
            }
        }

    private fun setValidateEmailVisibility(email: String) =
        userDetailsDialogFragment?.run {
            if (viewModel.validateEmail(email)) {
                setValidateEmailHintVisibility(INVISIBLE)
            } else {
                setValidateEmailHintVisibility(VISIBLE)
            }
        }

    private fun setValidateConfirmEmailVisibility(confirmEmail: String) =
        userDetailsDialogFragment?.run {
            if (viewModel.validateConfirmEmail(confirmEmail)) {
                setValidateConfirmEmailHintVisibility(INVISIBLE)
            } else {
                setValidateConfirmEmailHintVisibility(VISIBLE)
            }
        }

    private fun setupUserDetailsDialog() {
        userDetailsDialogFragment = UserDetailsDialogFragment(
            sendUserDetails = { name, email -> viewModel.sendUserDetailsOnClick(name, email) },
            validateName = { name -> viewModel.validateName(name) },
            validateEmail = { email -> viewModel.validateEmail(email) },
            validateConfirmEmail = { confirmEmail -> viewModel.validateConfirmEmail(confirmEmail) },
            done = { viewModel.dismissDialogOnClick() }
        )
    }

    private fun showUserDetailsDialogFragment() =
        if (userDetailsDialogFragment?.dialog?.isShowing == true) {
            Unit
        } else {
            userDetailsDialogFragment?.show(
                childFragmentManager,
                UserDetailsDialogFragment.USER_DETAILS_TAG
            )
        }

    private fun showCancelInviteDialog() {

    }
}