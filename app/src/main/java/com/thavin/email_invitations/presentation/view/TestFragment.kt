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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thavin.email_invitations.R
import com.thavin.email_invitations.databinding.FragmentInvitationBinding
import com.thavin.email_invitations.databinding.FragmentTestBinding
import com.thavin.email_invitations.presentation.adapter.TestAdapter
import com.thavin.email_invitations.presentation.model.ListItem
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.InvitationUiEvent.*
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.UserDetailsUiEvent.*
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.CancelInviteUiEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class TestFragment : Fragment() {

    private val viewModel: InvitationViewModel by activityViewModels()

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private var itemAdapter: TestAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = listOf(ListItem(UUID.randomUUID(), "1"), ListItem(UUID.randomUUID(), "2"))


        with(binding.recyclerviewRequestInvite) {
            itemAdapter = TestAdapter()
            adapter = itemAdapter
        }

        itemAdapter?.submitList(list)

        viewModel.checkInviteStatus()
        collectInvitationUiEvents()
    }

    private fun collectInvitationUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.invitationUiEvent.collect {
                    when (it) {
                        is ShowPostInviteScreen -> binding.textviewSubtext.setText(R.string.registered)
                        is ShowPreInviteScreen -> binding.textviewSubtext.setText(R.string.not_registered)
                        else -> {}
                    }
                }
            }
        }
}