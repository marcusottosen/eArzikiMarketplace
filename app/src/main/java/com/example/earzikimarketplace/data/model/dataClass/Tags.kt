package com.example.earzikimarketplace.data.model.dataClass

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.earzikimarketplace.R

data class TagItem(
    val category: String,
    val icon: ImageVector
)

enum class TagEnum(val id: Int, @StringRes val titleId: Int, val icon: Int) {
    DECORATIVES(1,          R.string.decoratives, R.drawable.decoratives),
    SPORTS(2,               R.string.tag_sports, R.drawable.basketball),
    ELECTRONICS(3,          R.string.tag_electronics, R.drawable.phone_portrait),
    MATERIALS(4,            R.string.tag_materials, R.drawable.material),
    BOOKS(5,                R.string.tag_books, R.drawable.book),
    ACCESSORIES(6,          R.string.tag_accessories, R.drawable.accessories),
    KITCHEN(7,              R.string.tag_kitchen, R.drawable.kitchen),
    CLOTHES(8,              R.string.tag_clothes, R.drawable.clothes),
    TECHNOLOGY(9,           R.string.tag_technology, R.drawable.calculator),
    HOMEAPPLIANCES(10,      R.string.tag_home_appliances, R.drawable.home),
    BUILDINGMATERIALS(11,   R.string.tag_building_materials, R.drawable.hammer),
    TEXTILES(12,            R.string.tag_textiles, R.drawable.textile),
    TOYS(13,                R.string.tag_toys, R.drawable.toys),
    Tools(14,               R.string.tag_tools, R.drawable.tool),
    VEHICLES(15,            R.string.tag_vehicles, R.drawable.basketball);

    fun getTitle(context: Context): String {
        return context.getString(titleId)
    }

    companion object {
        fun fromId(id: Int): TagEnum? {
            return values().find { it.id == id }
        }

        fun getAllTags(): List<TagEnum> {
            return values().toList()
        }
    }
}

