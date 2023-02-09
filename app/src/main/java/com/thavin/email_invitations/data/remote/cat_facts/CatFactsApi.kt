package com.thavin.email_invitations.data.remote.cat_facts

import com.thavin.email_invitations.data.remote.cat_facts.dto.Facts
import retrofit2.http.GET

interface CatFactsApi {

    companion object {
        const val BASE_URL = "https://cat-fact.herokuapp.com"
    }

    @GET("/facts")
    suspend fun getCatFacts(): List<Facts>
}