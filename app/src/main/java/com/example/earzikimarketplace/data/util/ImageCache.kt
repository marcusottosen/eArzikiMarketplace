package com.example.earzikimarketplace.data.util

import androidx.compose.ui.graphics.ImageBitmap

object ImageCache {
    private val cache = mutableMapOf<String, ImageBitmap>()

    fun put(url: String, bitmap: ImageBitmap) {
        cache[url] = bitmap
    }

    fun get(url: String): ImageBitmap? = cache[url]
}
