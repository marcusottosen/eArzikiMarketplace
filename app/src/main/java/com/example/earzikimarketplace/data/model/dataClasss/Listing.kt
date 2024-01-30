package com.example.earzikimarketplace.data.model.dataClasss

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.Date
import java.util.Locale



@Serializable
data class Listing(
    @Serializable(with = UUIDSerializer::class)
    val listing_id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID? = null,
    val title: String = "",
    val description: String = "",
    val category_id: Int = 0,
    val price: Float = 0f,
    val image_urls: List<String>? = listOf(),
    val active: Boolean = true,
    @Serializable(with = DateSerializer::class)
    val post_date: Date? = null,
    val ListingTags: List<ListingTag>? = listOf()
)


object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        // Remove the microseconds part of the date string
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

@Serializable
data class ListingTag(
    val tag_id: Int
)
