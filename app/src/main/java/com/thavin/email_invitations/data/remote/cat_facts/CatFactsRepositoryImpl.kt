package com.thavin.email_invitations.data.remote.cat_facts

import com.thavin.email_invitations.data.remote.cat_facts.mapper.mapFactsToCatFacts
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFactsResource
import com.thavin.email_invitations.data.remote.cat_facts.repository.CatFactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CatFactsRepositoryImpl(
    private val catFactsApi: CatFactsApi
) : CatFactsRepository {

    override suspend fun getCatFacts(): Flow<CatFactsResource> =
        flow {
            try {
                emit(CatFactsResource.Loading)
                val facts = catFactsApi.getCatFacts()
                emit(CatFactsResource.Success(facts = mapFactsToCatFacts(facts)))
            } catch (e: Exception) {
                emit(CatFactsResource.Error(e))
            }
        }
}