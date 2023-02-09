package com.thavin.email_invitations.data.remote.cat_facts.mapper

import com.thavin.email_invitations.data.remote.cat_facts.dto.Facts
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFacts
import java.util.*

fun mapFactsToCatFacts(facts: List<Facts>): MutableList<CatFacts> =
    facts.map {
        val id = UUID.randomUUID()
        CatFacts(id, it.text)
    }.toMutableList()
