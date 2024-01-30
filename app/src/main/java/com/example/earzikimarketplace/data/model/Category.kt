package com.example.earzikimarketplace.data.model

import com.example.earzikimarket.R

data class Category(
    val categoryID: Int,
    val title: String,
    var itemCount: Int = 0,
    val imageRes: Int   // Drawable
)
class UninitializedCountException : Exception("Count not initialized")

data class DBCategory(
    val categoryID: Int,
    val categoryName: String,
    val imageRes: Int,
    var itemCount: Result<Int> = Result.failure(UninitializedCountException())
){
    companion object{
        val defaultCategories = CategoryEnum.values().map { enumItem ->
            DBCategory(enumItem.id, enumItem.title, enumItem.imageRes)
        }
        /*val defaultCategories = listOf(
            DBCategory(1, "Home Crafts", R.drawable.home_crafts_image),
            DBCategory(2, "Used Items", R.drawable.used_items_image),
            DBCategory(3, "Food & Agriculture", R.drawable.food_image),
            DBCategory(4, "Local Services", R.drawable.local_services_image ),
            DBCategory(5, "Local Offers from Shops and Restaurants", R.drawable.local_offers_image),
        )*/

        fun getCategoryById(id: Int) = defaultCategories.find { it.categoryID == id }
        fun getCategoryByName(name: String) = defaultCategories.find { it.categoryName.equals(name, ignoreCase = true) }
    }
}

enum class CategoryEnum(val id: Int, val title: String, val imageRes: Int) {
    HOME_CRAFTS(1, "Home Crafts", R.drawable.home_crafts_image),
    USED_ITEMS(2, "Used Items", R.drawable.used_items_image),
    FOOD(3, "Food & Agriculture", R.drawable.food_image),
    LOCAL_SERVICES(4, "Local Services", R.drawable.local_services_image ),
    LOCAL_OFFERS(5, "Local Offers from Shops and Restaurants", R.drawable.local_offers_image),
    ;

    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
    }
}