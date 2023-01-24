package com.thavin.email_invitations.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thavin.email_invitations.R
import com.thavin.email_invitations.databinding.FragmentFirstBinding
import com.thavin.email_invitations.presentation.viewmodel.EmailInvitationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailInvitationFragment : Fragment() {

    private val viewModel: EmailInvitationViewModel by viewModels()

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            viewModel.onRequestInviteClicked(binding.edittextName.text.toString(), binding.edittextEmail.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}