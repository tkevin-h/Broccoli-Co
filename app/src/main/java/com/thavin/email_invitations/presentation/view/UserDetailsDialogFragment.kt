package com.thavin.email_invitations.presentation.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.thavin.email_invitations.R

class UserDetailsDialogFragment : DialogFragment() {

    private lateinit var sendUserDetailsListener: (name: String, email: String) -> Unit
    private lateinit var nameValidationListener: (name: String) -> Unit
    private lateinit var emailValidationListener: (email: String) -> Unit
    private lateinit var confirmEmailValidationListener: (email: String) -> Unit
    private lateinit var binding: View

    companion object {
        const val USER_DETAILS_TAG = "RewardsDetailsTag"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = requireActivity().layoutInflater.inflate(R.layout.dialog_user_details, null)

        binding.findViewById<Button>(R.id.button_request_invite).setOnClickListener {
            val name = binding.findViewById<EditText>(R.id.edittext_name).text.toString()
            val email = binding.findViewById<EditText>(R.id.edittext_email).text.toString()
            val confirmEmail = binding.findViewById<EditText>(R.id.edittext_confirm_email).text.toString()

            nameValidationListener(name)
            emailValidationListener(email)
            confirmEmailValidationListener(confirmEmail)
            sendUserDetailsListener(name, email)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding)
            .create()
    }

    fun setSendUserDetailsListener(listener: (name: String, email: String) -> Unit) {
        sendUserDetailsListener = listener
    }

    fun setNameValidationListener(listener: (name: String) -> Unit) {
        nameValidationListener = listener
    }

    fun setEmailValidationListener(listener: (email: String) -> Unit) {
        emailValidationListener = listener
    }

    fun setConfirmEmailValidationListener(listener: (email: String) -> Unit) {
        confirmEmailValidationListener = listener
    }

    fun setValidateNameHintVisibility(visibility: Int) {
        binding.findViewById<TextView>(R.id.textview_name_validation).visibility = visibility
    }

    fun setValidateEmailHintVisibility(visibility: Int) {
        binding.findViewById<TextView>(R.id.textview_email_validation).visibility = visibility
    }

    fun setValidateConfirmEmailHintVisibility(visibility: Int) {
        binding.findViewById<TextView>(R.id.textview_confirm_email_validation).visibility = visibility
    }

    fun showLoadingProgressBar() {
        binding.findViewById<Button>(R.id.button_request_invite).visibility = View.INVISIBLE
        binding.findViewById<ProgressBar>(R.id.progress_request_invite).visibility = VISIBLE
    }

    fun showSendButton() {
        binding.findViewById<ProgressBar>(R.id.progress_request_invite).visibility = View.INVISIBLE
        binding.findViewById<Button>(R.id.button_request_invite).visibility = VISIBLE
    }
}