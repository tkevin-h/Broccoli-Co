package com.thavin.email_invitations.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thavin.email_invitations.data.InvitationApi
import com.thavin.email_invitations.data.InvitationRepository
import com.thavin.email_invitations.data.InvitationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesInvitationApi(json: Json, client: OkHttpClient): InvitationApi {
        return Retrofit.Builder()
            .baseUrl(InvitationApi.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(InvitationApi::class.java)
    }

    @Provides
    @Singleton
    fun providesInvitationRepository(
        invitationApi: InvitationApi
    ): InvitationRepository {
        return InvitationRepositoryImpl(invitationApi)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            this
                .addInterceptor(interceptor = interceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun providesJsonBuilder(): Json =
        Json { isLenient = true }
}