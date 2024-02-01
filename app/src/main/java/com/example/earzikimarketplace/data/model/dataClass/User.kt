package com.example.earzikimarketplace.data.model.dataClass

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val created_at: Date? = null,
    val email: String,
    val firstname: String,
    val surname: String,
    val location_id: Int? = 0,
    val profile_picture: Int? = 0,
    val phone_number: Int,
    val age: Int = 18,
)


@Serializable
data class UserSignUp(
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID?,
    val email: String?,
    val firstname: String?,
    val surname: String?,
    val location_id: Int? = 0,
    val profile_picture: Int? = 0,
    val phone_number: Int?,
    val age: Int? = 18,
    val created_at: Instant? = null,
)