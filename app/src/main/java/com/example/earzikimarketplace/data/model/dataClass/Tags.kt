package com.example.earzikimarketplace.data.model.dataClass

import android.content.Context
import androidx.annotation.StringRes
import com.example.earzikimarketplace.R
import kotlinx.serialization.Serializable

/**
 * Model class representing a tag attachment.
 * @property listing_id The ID of the listing.
 * @property tag_id The ID of the tag attached to the listing.
 */
@Serializable
data class TagAttachment(
    val listing_id: String, // UUID serialized as String
    val tag_id: Int
)

/**
 * Enum representing different tags.
 * @property id the ID of the tag
 * @property titleId The resource ID of the title of the tag.
 * @property icon The resource ID of the icon representing the tag.
 */
enum class TagEnum(val id: Int, @StringRes val titleId: Int, val icon: Int) {
    DECORATIVES(1, R.string.decoratives, R.drawable.decoratives),
    SPORTS(2, R.string.tag_sports, R.drawable.basketball),
    ELECTRONICS(3, R.string.tag_electronics, R.drawable.phone_portrait),
    MATERIALS(4, R.string.tag_materials, R.drawable.material),
    BOOKS(5, R.string.tag_books, R.drawable.book),
    ACCESSORIES(6, R.string.tag_accessories, R.drawable.accessories),
    KITCHEN(7, R.string.tag_kitchen, R.drawable.kitchen),
    CLOTHES(8, R.string.tag_clothes, R.drawable.clothes),
    TECHNOLOGY(9, R.string.tag_technology, R.drawable.calculator),
    HOMEAPPLIANCES(10, R.string.tag_home_appliances, R.drawable.bulb),
    BUILDINGMATERIALS(11, R.string.tag_building_materials, R.drawable.hammer),
    TEXTILES(12, R.string.tag_textiles, R.drawable.textile),
    TOYS(13, R.string.tag_toys, R.drawable.toys),
    Tools(14, R.string.tag_tools, R.drawable.tool),
    VEHICLES(15, R.string.tag_vehicles, R.drawable.basketball);

    /**
     * Retrieves the title of the tag.
     * @param context The context to fetch localized titles.
     * @return The title of the tag.
     */
    fun getTitle(context: Context): String {
        return context.getString(titleId)
    }

    companion object {
        /**
         * Retrieves a tag enum by its ID.
         * @param id The ID of the tag enum to retrieve.
         * @return The tag enum with the specified ID, or null if not found.
         */
        fun fromId(id: Int): TagEnum? {
            return values().find { it.id == id }
        }

        /**
         * Retrieves all tags.
         * @return List of all tags.
         */
        fun getAllTags(): List<TagEnum> {
            return values().toList()
        }
    }
}

