package com.thavin.email_invitations.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.thavin.email_invitations.data.local.repository.InviteStatusRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class InviteStatusUseCase(
    private val dataStore: DataStore<Preferences>
) : InviteStatusRepository {

    companion object {
        private val dataStoreKey = booleanPreferencesKey("invite_status")
    }

    override suspend fun setInviteStatus(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = status
        }
    }

    override suspend fun getInviteStatus(): Boolean {
        val status = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[dataStoreKey] ?: false
            }

        return status.first()
    }
}