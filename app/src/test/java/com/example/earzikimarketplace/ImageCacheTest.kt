package com.example.earzikimarketplace

import com.example.earzikimarketplace.data.util.ImageCache
import androidx.compose.ui.graphics.ImageBitmap
import org.junit.jupiter.api.Test
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach

class ImageCacheTest {

    private lateinit var bitmap: ImageBitmap
    private val key = "testKey"

    @BeforeEach
    fun setUp() {
        // Setup common objects used in the tests
        bitmap = mockk(relaxed = true)
        ImageCache.clear() // Ensure the cache is clear before each test
    }

    @Test
    fun `put and get image from cache`() {
        // Add an image to the cache
        ImageCache.put(key, bitmap)

        // Retrieve the image
        val result = ImageCache.get(key)

        // Verify the retrieved image is not null and is the same as what was put in
        assertSame(bitmap, result, "The retrieved bitmap should be the same as the stored one.")
    }

    @Test
    fun `get image from cache returns null for unknown key`() {
        // Attempt to retrieve an image with a key that hasn't been used to put anything in the cache
        val result = ImageCache.get("unknownKey")

        // Verify that the result is null
        assertNull(result, "Getting an image with an unknown key should return null.")
    }

    @Test
    fun `clear removes all images from cache`() {
        // Add an image to the cache
        ImageCache.put(key, bitmap)

        // Clear the cache
        ImageCache.clear()

        // Attempt to retrieve the previously stored image
        val result = ImageCache.get(key)

        // Verify that the result is null, indicating the cache has been cleared
        assertNull(result, "After clearing, the cache should not return any images.")
    }
}