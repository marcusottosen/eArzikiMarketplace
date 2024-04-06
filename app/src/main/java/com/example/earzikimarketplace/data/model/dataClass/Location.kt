package com.example.earzikimarketplace.data.model.dataClass

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Model class representing a location.
 * @property location_id The unique identifier of the location.
 * @property country_code The country code of the location.
 * @property city The city of the location.
 * @property address The address of the location.
 */
@Serializable
data class Location(
    @Serializable(with = UUIDSerializer::class)
    val location_id: UUID,
    val country_code: Int? = 0,
    val city: String?,
    val address: String?
)