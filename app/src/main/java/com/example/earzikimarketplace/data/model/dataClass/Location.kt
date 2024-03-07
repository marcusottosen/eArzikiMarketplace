package com.example.earzikimarketplace.data.model.dataClass

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Location(
    @Serializable(with = UUIDSerializer::class)
    val location_id: UUID,
    val country_code: Int? = 0,
    val city: String?,
    val address: String?
)