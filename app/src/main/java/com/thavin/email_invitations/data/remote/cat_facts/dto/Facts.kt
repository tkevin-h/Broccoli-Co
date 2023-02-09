package com.thavin.email_invitations.data.remote.cat_facts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Facts(
    val status: Status,
    @SerialName("_id")
    val id: String,
    val user: String,
    val text: String,
    @SerialName("__v")
    val v: Long,
    val source: String,
    val updatedAt: String,
    val type: String,
    val createdAt: String,
    val deleted: Boolean,
    val used: Boolean
)

@Serializable
data class Status(
    val verified: Boolean,
    val sentCount: Long
)