package com.thavin.email_invitations.data.remote.cat_facts.model

sealed class CatFactsResource {
    data class Success(val facts: MutableList<CatFacts>, val message: String? = null) :
        CatFactsResource()

    object Loading : CatFactsResource()
    data class Error(val exception: Exception? = null, val message: String? = null) :
        CatFactsResource()
}