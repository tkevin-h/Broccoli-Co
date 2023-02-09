package com.thavin.email_invitations.data.remote.cat_facts.repository

import com.thavin.email_invitations.data.remote.cat_facts.model.CatFactsResource
import kotlinx.coroutines.flow.Flow

interface CatFactsRepository {

    suspend fun getCatFacts(): Flow<CatFactsResource>
}