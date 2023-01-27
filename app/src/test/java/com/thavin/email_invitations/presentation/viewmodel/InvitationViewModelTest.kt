package com.thavin.email_invitations.presentation.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thavin.email_invitations.data.local.fake.FakeInviteStatusUseCase
import com.thavin.email_invitations.data.remote.fake.FakeRequestInviteUseCase
import com.thavin.email_invitations.presentation.viewmodel.InvitationViewModel.UiEvent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class InvitationViewModelTest {

    private lateinit var viewModel: InvitationViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val fakeRequestInviteUseCase = FakeRequestInviteUseCase()
    private val fakeInviteStatusUseCase = FakeInviteStatusUseCase()

    @Before
    fun beforeTests() {
        viewModel = InvitationViewModel(
            fakeRequestInviteUseCase,
            fakeInviteStatusUseCase
        )
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun afterTests() {
        Dispatchers.resetMain()
    }

    @Test
    fun `requesting an invite`() = runTest {
        viewModel.requestInviteOnClick()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(RequestInviteOnClick)
        }
    }

    @Test
    fun `requesting to cancel an invite`() = runTest {
        viewModel.requestCancelInviteOnClick()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(CancelInviteOnClick)
        }
    }

    @Test
    fun `sending user details returns a success`() = runTest {
        fakeRequestInviteUseCase.shouldReturnError(false)

        viewModel.sendUserDetailsOnClick("testName", "testEmail@test.com", "testEmail@test.com")

        viewModel.uiEvent.test {
            listOf(
                ValidName,
                ValidEmail,
                ValidConfirmEmail,
                InviteDetailsLoading,
                InviteDetailsSuccess
            ).forEach { uiEvent ->
                val emission = awaitItem()
                assertThat(emission).isEqualTo(uiEvent)
            }
        }
    }

    @Test
    fun `sending user details returns an error`() = runTest {
        fakeRequestInviteUseCase.shouldReturnError(true)
        viewModel.sendUserDetailsOnClick("testName", "testEmail@test.com", "testEmail@test.com")

        viewModel.uiEvent.test {
            listOf(
                ValidName,
                ValidEmail,
                ValidConfirmEmail,
                InviteDetailsLoading,
                InviteDetailsError("error")
            ).forEach { uiEvent ->
                val emission = awaitItem()
                assertThat(emission).isEqualTo(uiEvent)
            }
        }
    }

    @Test
    fun `sending invalid user details returns validation error`() = runTest {
        fakeRequestInviteUseCase.shouldReturnError(true)
        viewModel.sendUserDetailsOnClick("sam", "testEmail", "testEmail@test.com")

        viewModel.uiEvent.test {
            listOf(
                InvalidName,
                InvalidEmail,
                InvalidConfirmEmail
            ).forEach { uiEvent ->
                val emission = awaitItem()
                assertThat(emission).isEqualTo(uiEvent)
            }
        }
    }

    @Test
    fun `dismissing the invite details dialog`() = runTest {
        viewModel.dismissInviteDetailsDialogOnClick()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(DismissInviteDetailsDialogOnClick)
        }
    }

    @Test
    fun `invited status displays the post invite screen`() = runTest {
        fakeInviteStatusUseCase.setInvitedStatus(true)
        viewModel.checkInviteStatus()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(ShowPostInviteScreen)
        }
    }

    @Test
    fun `not invited status returns the pre invite screen`() = runTest {
        fakeInviteStatusUseCase.setInvitedStatus(false)
        viewModel.checkInviteStatus()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(ShowPreInviteScreen)
        }
    }

    @Test
    fun `cancelling a current invite`() = runTest {
        viewModel.cancelInviteOnClick()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(CancelInviteSuccess)
        }
    }

    @Test
    fun `dismissing the cancel invite dialog`() = runTest {
        viewModel.dismissCancelInviteDialogOnClick()

        viewModel.uiEvent.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(DismissCancelInviteDialogOnClick)
        }
    }
}