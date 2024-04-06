package com.example.earzikimarketplace.data.model.dataClass

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Model class representing a sales listing.
 * @property listing_id The unique identifier of the listing.
 * @property user_id The unique identifier of the user who created the listing.
 * @property title The title of the listing.
 * @property description The description of the listing.
 * @property category_id The ID of the category to which the listing belongs.
 * @property price The price of the listing.
 * @property image_urls The URLs of the images associated with the listing.
 * @property active Indicates if the listing is active or not.
 * @property post_date The date when the listing was posted.
 * @property ListingTags The tags associated with the listing.
 */
@Serializable
data class Listing(
    @Serializable(with = UUIDSerializer::class) val listing_id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class) val user_id: UUID? = null,
    val title: String = "",
    val description: String = "",
    val category_id: Int = 0,
    val price: Float = 0f,
    val image_urls: List<String>? = listOf(),
    val active: Boolean = true,
    @Serializable(with = DateSerializer::class) val post_date: Date? = null,
    val ListingTags: List<ListingTag>? = listOf()
)

/**
 * Serializer for UUID.
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

/**
 * Serializer for Date.
 */
object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        // Removes the microseconds part of the date string
        val dateWithoutMicroseconds = dateString.substringBeforeLast(".")
        return dateFormat.parse(dateWithoutMicroseconds)
            ?: throw SerializationException("Error parsing date")
    }

    override fun serialize(encoder: Encoder, value: Date) {
        // Format the Date back to the string without microseconds.
        val dateString = dateFormat.format(value)
        encoder.encodeString(dateString)
    }
}

/**
 * Model class representing a listing tag.
 * @property tag_id The unique identifier of the tag.
 */
@Serializable
data class ListingTag(
    val tag_id: Int
)
