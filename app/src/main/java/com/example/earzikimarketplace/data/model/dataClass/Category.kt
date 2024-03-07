package com.example.earzikimarketplace.data.model.dataClass

import android.content.Context
import androidx.annotation.StringRes
import com.example.earzikimarket.R

class UninitializedCountException : Exception("Count not initialized")

data class DBCategory(
    val categoryID: Int,
    val categoryName: String,
    val imageRes: Int,
    var itemCount: Result<Int> = Result.failure(UninitializedCountException())
) {
    companion object {
        fun createDefaultCategories(context: Context): List<DBCategory> {
            return CategoryEnum.values().map { enumItem ->
                DBCategory(
                    enumItem.id,
                    enumItem.getTitle(context), // Fetch localized title using CategoryEnum's getTitle method
                    enumItem.imageRes
                )
            }
        }

        fun getCategoryById(id: Int, context: Context) = createDefaultCategories(context).find { it.categoryID == id }
        fun getCategoryByName(name: String, context: Context) = createDefaultCategories(context).find { it.categoryName.equals(name, ignoreCase = true) }
    }
}

enum class CategoryEnum(val id: Int, @StringRes val titleResId: Int, val imageRes: Int) {
    HOME_CRAFTS(1, R.string.category_home_crafts, R.drawable.home_crafts_image),
    USED_ITEMS(2, R.string.category_used_items, R.drawable.used_items_image),
    FOOD(3, R.string.category_food_agriculture, R.drawable.food_image),
    LOCAL_SERVICES(4, R.string.category_local_services, R.drawable.local_services_image),
    LOCAL_OFFERS(5, R.string.category_local_offers, R.drawable.local_offers_image),
    ;

    // Method to get localized title
    fun getTitle(context: Context): String {
        return context.getString(titleResId)
    }

    // Method to get category enum from id
    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
    }
}