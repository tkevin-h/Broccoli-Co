package com.thavin.email_invitations.presentation.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.thavin.email_invitations.R

class InviteDetailsDialogFragment(
    private val sendUserDetails: (name: String, email: String) -> Unit,
    private val validateName: (name: String) -> Unit,
    private val validateEmail: (name: String) -> Unit,
    private val validateConfirmEmail: (name: String) -> Unit,
    private val done: () -> Unit,
    private val checkInviteStatus: () -> Unit
) : DialogFragment() {

    private lateinit var binding: View

    companion object {
        const val USER_DETAILS_TAG = "RewardsDetailsTag"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = requireActivity().layoutInflater.inflate(R.layout.dialog_fragment_invite_details, null)

        setSuccessImage()

        binding.findViewById<Button>(R.id.button_request_invite).setOnClickListener {
            val name = binding.findViewById<EditText>(R.id.edittext_name).text.toString()
            val email = binding.findViewById<EditText>(R.id.edittext_email).text.toString()
            val confirmEmail = binding.findViewById<EditText>(R.id.edittext_confirm_email).text.toString()

            validateName(name)
            validateEmail(email)
            validateConfirmEmail(confirmEmail)
            sendUserDetails(name, email)
        }

        binding.findViewById<Button>(R.id.button_success).setOnClickListener {
            done()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding)
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        checkInviteStatus()
    }

    // Public Functions
    fun dismissDialog() {
        dismiss()
        checkInviteStatus()
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
        hideError()
        binding.findViewById<Button>(R.id.button_request_invite).visibility = View.INVISIBLE
        binding.findViewById<ProgressBar>(R.id.progress_request_invite).visibility = VISIBLE
    }

    fun showSuccessDialog() {
        binding.findViewById<CardView>(R.id.cardview_user_details).visibility = GONE
        binding.findViewById<CardView>(R.id.cardview_success).visibility = VISIBLE
    }

    fun showError(error: String? = null) {
        val errorTextView = binding.findViewById<TextView>(R.id.textview_send_error)

        error?.let {
            errorTextView.text = error
        } ?: run {
            errorTextView.text = getString(R.string.send_details_generic_error)
        }

        errorTextView.visibility = VISIBLE
        showSendButton()
    }

    // Private Functions
    private fun hideError() {
        binding.findViewById<TextView>(R.id.textview_send_error).visibility = View.INVISIBLE
    }

    private fun showSendButton() {
        binding.findViewById<ProgressBar>(R.id.progress_request_invite).visibility = View.INVISIBLE
        binding.findViewById<Button>(R.id.button_request_invite).visibility = VISIBLE
    }

    private fun setSuccessImage() {
        val imageView = binding.findViewById<ImageView>(R.id.imageview_success)

        Glide.with(this)
            .load(R.drawable.confetti)
            .placeholder(R.drawable.confetti)
            .into(imageView)
    }
}