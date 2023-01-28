package com.thavin.email_invitations.presentation.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.thavin.email_invitations.R

class CancelInviteDialog(
    private val cancelInvite: () -> Unit,
    private val done: () -> Unit,
    private val checkInviteStatus: () -> Unit
) : DialogFragment() {

    private lateinit var binding: View

    companion object {
        const val CANCEL_INVITE_TAG = "CancelInviteTag"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = requireActivity().layoutInflater.inflate(R.layout.dialog_cancel_invite, null)

        setSuccessImage()

        binding.findViewById<Button>(R.id.button_confirm_cancel).setOnClickListener {
            cancelInvite()
        }

        binding.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            done()
        }


        binding.findViewById<Button>(R.id.button_cancel_success).setOnClickListener {
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

    fun showSuccess() {
        binding.findViewById<CardView>(R.id.cardview_cancel_invite).visibility = GONE
        binding.findViewById<CardView>(R.id.cardview_cancel_success).visibility = VISIBLE
    }

    // Private Functions

    private fun setSuccessImage() {
        val imageView = binding.findViewById<ImageView>(R.id.imageview_cancel_success)

        Glide.with(this)
            .load(R.drawable.confetti)
            .placeholder(R.drawable.confetti)
            .into(imageView)
    }
}