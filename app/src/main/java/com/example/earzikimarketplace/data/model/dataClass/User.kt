package com.example.earzikimarketplace.data.model.dataClass

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

/**
 * Model class representing a user.
 * @property user_id the users ID
 * @property created_at The date when the user was created.
 * @property email The email address of the user.
 * @property firstname The first name of the user.
 * @property surname The last name of the user.
 * @property location_id The ID of the user's location.
 * @property profile_picture The resource ID of the user's profile picture.
 * @property phone_number The phone number of the user.
 * @property age The age of the user.
 */
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

/**
 * Model class representing user sign-up data which is used when handling the user locally.
 * @property user_id The users ID
 * @property email The email address of the user.
 * @property firstname The first name of the user.
 * @property surname The last name of the user.
 * @property location_id The ID of the user's location.
 * @property profile_picture The resource ID of the user's profile picture.
 * @property phone_number The phone number of the user.
 * @property age The age of the user.
 * @property created_at The date when the user signed up.
 */
@Serializable
data class UserSignUp(
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID?,
    val email: String?,
    val firstname: String?,
    val surname: String?,
    @Serializable(with = UUIDSerializer::class)
    val location_id: UUID?,
    val profile_picture: Int? = 0,
    val phone_number: Int?,
    val age: Int? = 18,
    val created_at: Instant? = null,
)