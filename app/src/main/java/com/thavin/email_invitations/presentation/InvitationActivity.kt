package com.thavin.email_invitations.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thavin.email_invitations.databinding.ActivityInvitationBinding
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvitationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}