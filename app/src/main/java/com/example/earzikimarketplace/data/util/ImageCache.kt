package com.example.earzikimarketplace.data.util

import androidx.compose.ui.graphics.ImageBitmap

object ImageCache {
    private val cache = mutableMapOf<String, ImageBitmap>()

    /**
     * Stores an image bitmap in the cache.
     * @param url The URL of the image.
     * @param bitmap The image bitmap to be cached.
     */
    fun put(url: String, bitmap: ImageBitmap) {
        cache[url] = bitmap
    }

    /**
     * Retrieves an image bitmap from the cache.
     * @param url The URL of the image.
     * @return The cached image bitmap, or null if not found.
     */
    fun get(url: String): ImageBitmap? = cache[url]

    /**
     * Clears the image cache. Only used for test.
     */
    fun clear() {
        cache.clear()
    }
}
