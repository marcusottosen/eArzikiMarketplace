package com.example.earzikimarketplace.data.model.dataClass

import androidx.annotation.StringRes
import com.example.earzikimarket.R

enum class SortOption(val id: Int, @StringRes val resourceId: Int) {
    NearestItems(0, R.string.nearest_items),
    DateNewest(1, R.string.date_newest),
    DateOldest(2, R.string.date_oldest),
    PriceCheapest(3, R.string.price_cheapest),
    PriceMostExpensive(4, R.string.price_most_expensive);
}
