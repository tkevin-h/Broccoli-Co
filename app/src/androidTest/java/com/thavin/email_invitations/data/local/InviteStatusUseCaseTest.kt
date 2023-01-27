package com.thavin.email_invitations.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InviteStatusUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher + Job())
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = scope,
        produceFile = { context.preferencesDataStoreFile("test_user_preferences") }
    )

    private val inviteStatusUseCase = InviteStatusUseCase(dataStore)

    @Before
    fun beforeTests() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun afterTests() {
        Dispatchers.resetMain()
        scope.cancel()
    }

    @Test
    fun getInviteStatusReturnsInvited() {
        val expectedStatus = true

        scope.runTest {
            inviteStatusUseCase.setInviteStatus(true)
            val actualStatus = inviteStatusUseCase.getInviteStatus()
            assertThat(actualStatus).isEqualTo(expectedStatus)

            dataStore.edit { it.clear() }
        }
    }

    @Test
    fun getInviteStatusReturnsNotInvited() {
        val expectedStatus = false

        scope.runTest {
            inviteStatusUseCase.setInviteStatus(false)
            val actualStatus = inviteStatusUseCase.getInviteStatus()
            assertThat(actualStatus).isEqualTo(expectedStatus)

            dataStore.edit { it.clear() }
        }
    }
}