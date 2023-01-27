package com.thavin.email_invitations.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thavin.email_invitations.data.local.repository.InviteStatusRepository
import com.thavin.email_invitations.data.local.InviteStatusUseCase
import com.thavin.email_invitations.data.remote.RequestInviteApi
import com.thavin.email_invitations.data.remote.repository.RequestInviteRepository
import com.thavin.email_invitations.data.remote.RequestInviteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InvitationModule {

    private const val APPLICATION_JSON = "application/json"
    private const val USER_PREFERENCES = "user_preferences"

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideInvitationApi(json: Json, client: OkHttpClient): RequestInviteApi {
        return Retrofit.Builder()
            .baseUrl(RequestInviteApi.BASE_URL)
            .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
            .client(client)
            .build()
            .create(RequestInviteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInvitationRepository(requestInviteApi: RequestInviteApi): RequestInviteRepository {
        return RequestInviteUseCase(requestInviteApi)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            this
                .addInterceptor(interceptor = interceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideJsonBuilder(): Json =
        Json { isLenient = true }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): InviteStatusRepository {
        return InviteStatusUseCase(dataStore)
    }
}