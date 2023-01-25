package com.thavin.email_invitations.data

import com.thavin.email_invitations.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmailInvitationApi {

    companion object {
        const val BASE_URL = "https://us-central1-blinkapp-684c1.cloudfunctions.net/"
    }

    @Headers("Accept: application/json")
    @POST("/fakeAuth")
    suspend fun postUserInfo(@Body userInfo: UserInfo): Response<String>
}