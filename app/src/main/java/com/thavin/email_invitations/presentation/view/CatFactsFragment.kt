package com.thavin.email_invitations.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thavin.email_invitations.R
import com.thavin.email_invitations.presentation.adapter.TestAdapter
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFacts
import com.thavin.email_invitations.databinding.FragmentCatFactsBinding
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatFactsFragment : Fragment() {

    private val viewModel: InvitationViewModel by activityViewModels()

    private var _binding: FragmentCatFactsBinding? = null
    private val binding get() = _binding!!

    private var itemAdapter: TestAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.recyclerViewCatFacts) {
            itemAdapter = TestAdapter()
            adapter = itemAdapter
        }

        viewModel.getAstros()
        viewModel.checkInviteStatus()
        collectInvitationUiEvents()
        collectCatFactsUiEvents()
    }

    private fun collectInvitationUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
               with(binding){
                   viewModel.invitationUiEvent.collect {
                       when (it) {
                           is InvitationViewModel.InvitationUiEvent.ShowPreInviteScreen ->
                               textviewSubtext.setText(R.string.registered)
                           is InvitationViewModel.InvitationUiEvent.ShowPostInviteScreen ->
                               textviewSubtext.setText(R.string.not_registered)
                           else -> {}
                       }
                   }
               }
            }
        }


    private fun collectCatFactsUiEvents() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(binding) {
                    viewModel.astroUiEvent.collect {
                        when (it) {
                            is InvitationViewModel.CatFactsUiEvent.ShowCatFactsList -> {
                                progressBarCatFacts.visibility = GONE
                                recyclerViewCatFacts.visibility = VISIBLE
                                setFactsList(it.facts)
                            }
                            is InvitationViewModel.CatFactsUiEvent.ShowLoading -> {
                                recyclerViewCatFacts.visibility = GONE
                                progressBarCatFacts.visibility = VISIBLE
                            }
                            else -> {
                                recyclerViewCatFacts.visibility = GONE
                                progressBarCatFacts.visibility = GONE
                            }
                        }
                    }
                }
            }
        }

    private fun setFactsList(facts: MutableList<CatFacts>?) {
        itemAdapter?.submitList(facts)
    }
}