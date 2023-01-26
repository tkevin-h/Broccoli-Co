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
import com.thavin.email_invitations.presentation.viewmodel.EmailInvitationViewModel.UiEvent.*
import com.thavin.email_invitations.presentation.widget.UserDetailsDialogFragment
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
        setupUserDetailsDialogFragment()
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
                        is RequestInviteOnClick -> showUserDetailsDialogFragment()
                        is InvalidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(VISIBLE)
                        is ValidName -> userDetailsDialogFragment?.setValidateNameHintVisibility(INVISIBLE)
                        is InvalidEmail -> userDetailsDialogFragment?.setValidateEmailHintVisibility(VISIBLE)
                        is ValidEmail -> userDetailsDialogFragment?.setValidateEmailHintVisibility(INVISIBLE)
                        is InvalidConfirmEmail -> userDetailsDialogFragment?.setValidateConfirmEmailHintVisibility(VISIBLE)
                        is ValidConfirmEmail -> userDetailsDialogFragment?.setValidateConfirmEmailHintVisibility(INVISIBLE)
                        is SendUserDetailsLoading -> userDetailsDialogFragment?.showLoadingProgressBar()
                        is SendUserDetailsComplete -> userDetailsDialogFragment?.showSuccess()
                        is SendUserDetailsError -> userDetailsDialogFragment?.showError(it.message)
                        is DismissDialogOnClick -> userDetailsDialogFragment?.dismissDialog()
                        is CancelInviteOnClick -> {}
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
                viewModel.cancelInviteOnClick()
            }
        }

    private fun setupUserDetailsDialogFragment() {
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