package com.example.earzikimarketplace.data.model.dataClass

import android.content.Context
import androidx.annotation.StringRes
import com.example.earzikimarketplace.R

class UninitializedCountException : Exception("Count not initialized")

/**
 * Represents a category in the database.
 * @property categoryID The unique identifier of the category.
 * @property categoryName The name of the category.
 * @property imageRes The resource ID of the image representing the category.
 * @property itemCount The count of items in the category.
 * @throws UninitializedCountException If the count of items is accessed before initialization.
 */
data class DBCategory(
    val categoryID: Int,
    val categoryName: String,
    val imageRes: Int,
    var itemCount: Result<Int> = Result.failure(UninitializedCountException())
) {
    companion object {
        /**
         * Creates a list of default categories.
         * @param context The context to fetch localized titles.
         * @return List of default categories.
         */
        fun createDefaultCategories(context: Context): List<DBCategory> {
            return CategoryEnum.values().map { enumItem ->
                DBCategory(
                    enumItem.id,
                    enumItem.getTitle(context), // Fetch localized title using CategoryEnum's getTitle method
                    enumItem.imageRes
                )
            }
        }

        fun getCategoryById(id: Int, context: Context) =
            createDefaultCategories(context).find { it.categoryID == id }

        fun getCategoryByName(name: String, context: Context) =
            createDefaultCategories(context).find {
                it.categoryName.equals(
                    name,
                    ignoreCase = true
                )
            }
    }
}

/**
 * Enum representing different categories.
 * @property id The unique identifier of the category.
 * @property titleResId The resource ID of the localized title of the category.
 * @property imageRes The resource ID of the image representing the category.
 */
enum class CategoryEnum(val id: Int, @StringRes val titleResId: Int, val imageRes: Int) {
    HOME_CRAFTS(1, R.string.category_home_crafts, R.drawable.home_crafts_image),
    USED_ITEMS(2, R.string.category_used_items, R.drawable.used_items_image),
    FOOD(3, R.string.category_food_agriculture, R.drawable.food_image),
    LOCAL_SERVICES(4, R.string.category_local_services, R.drawable.local_services_image),
    LOCAL_OFFERS(5, R.string.category_local_offers, R.drawable.local_offers_image),
    ;

    /**
     * Retrieves the localized title of the category.
     * @param context The context to fetch localized titles.
     * @return The localized title of the category.
     */
    fun getTitle(context: Context): String {
        return context.getString(titleResId)
    }

    /**
     * Retrieves a category enum by its ID.
     * @param id The ID of the category enum to retrieve.
     * @return The category enum with the specified ID, or null if not found.
     */
    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
    }
}