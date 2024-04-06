package com.example.earzikimarketplace.data.model.supabaseAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.TagAttachment
import com.example.earzikimarketplace.data.model.dataClass.TagEnum
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.FilterOperator
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.TextSearchType
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.util.UUID

/**
 * Adapter class for interacting with the Listings table in Supabase.
 */
class ListingsDB() {
    private val tableName = "Listings"

    /**
     * Gets a list of items within a category from the database working with tags too.
     * @param start The start index of the items to retrieve.
     * @param pageSize The number of items per page.
     * @param category The ID of the category to retrieve items from.
     * @param tagId The ID of the tag to filter items by (optional).
     * @param sortByDateDescending Whether to sort items by date in descending order.
     * @param sortByPrice Whether to sort items by price.
     * @param priceAscending Whether to sort prices in ascending order.
     * @return A Result containing the list of retrieved listings.
     */
    suspend fun getItems(
        start: Long,
        pageSize: Long,
        category: Int,
        tagId: Int? = null,
        sortByDateDescending: Boolean = true,
        sortByPrice: Boolean = false, // If it should be sorted by price or date
        priceAscending: Boolean = true
    ): Result<List<Listing>> {
        val order = when {
            sortByPrice -> if (priceAscending) Order.ASCENDING else Order.DESCENDING
            else -> if (sortByDateDescending) Order.DESCENDING else Order.ASCENDING
        }
        val sortColumn = if (sortByPrice) "price" else "post_date"

        return try {
            val client = SupabaseManager.getClient()
            val response = if (tagId == null) {    // If no tag is picked
                client.postgrest[tableName].select {
                    range(start until start + pageSize)
                    order(sortColumn, order)
                    eq("active", "TRUE")
                    eq("category_id", category)
                }
            } else {    // If tag is picked
                val columns = Columns.raw("*, ListingTags!inner(tag_id)")
                client.postgrest["Listings"].select(columns = columns) {
                    range(start until start + pageSize)
                    order(sortColumn, order)
                    eq("active", "TRUE")
                    eq("category_id", category)
                    filter(
                        column = "ListingTags.tag_id",
                        operator = FilterOperator.EQ,
                        value = tagId
                    )
                }
            }
            val data = response.decodeList<Listing>()
            Result.success(data)
        } catch (e: Exception) {
            Log.e("supabase", "Error getting items: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Searches for listings by title.
     * @param searchQuery The search query.
     * @return A Result containing the list of retrieved listings.
     */
    suspend fun searchListingsByTitle(
        searchQuery: String
    ): Result<List<Listing>> {
        return try {
            val client = SupabaseManager.getClient()
            val response = client.postgrest["users"].select {
                textSearch(
                    column = "firstname",
                    query = "my",
                    textSearchType = TextSearchType.PLAINTO
                )
            }
            val data = response.decodeList<Listing>()
            Result.success(data)
        } catch (e: Exception) {
            Log.e("supabase", "Error searching listings by title: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Retrieves the image of an item from Supabase Storage.
     * @param context The context.
     * @param url The URL of the image.
     * @return The image data as a ByteArray.
     */
    @SuppressLint("ResourceType")
    suspend fun getItemImage(context: Context, url: String): ByteArray {
        val client = SupabaseManager.getClient()
        val bucket = client.storage["itemimages"]
        return try {
            // Download the image from db
            bucket.downloadAuthenticated(url)
        } catch (e: Exception) {
            // If fails, use the placeholder
            context.resources.openRawResource(R.drawable.placeholder).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                    ByteArrayOutputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.toByteArray()
                    }
                } ?: throw RuntimeException("Failed to decode placeholder image.")
            }
        }
    }


    /**
     * Gets the amount of items in a category by counting the active items.
     * @param category The ID of the category.
     * @return A Result containing the count of items.
     */
    suspend fun getCategoryCount(
        category: Int
    ): Result<Int> {

        return try {

            val client = SupabaseManager.getClient()

            val response = client.postgrest[tableName].select(columns = Columns.list("active")) {
                eq("active", "TRUE")
                eq("category_id", category)
            }
            val data = response.decodeList<ActiveRecord>()
            Result.success(data.size)
        } catch (e: Exception) {
            Log.e("supabaseDEBUG", "Error getting category count: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * data class representing an active record.
     * @property active Whether the record is active.
     */
    @Serializable
    data class ActiveRecord(val active: Boolean)


    /**
     * Adds a new item to the database.
     * @param listing The listing to add.
     * @return The ID of the added item.
     */
    suspend fun addItem(
        listing: Listing
    ): UUID {
        val client = SupabaseManager.getClient()
        try {
            client.postgrest[tableName].insert(listing)
            return listing.listing_id
        } catch (e: Exception) {
            Log.e("supabase", "Error adding new item: ${e.message}")
            throw e // Rethrow the exception back
        }
    }

    /**
     * Attaches tags to a listing.
     * @param listingID The ID of the listing.
     * @param tags The list of tags to attach.
     */
    suspend fun attachTags(listingID: UUID, tags: List<TagEnum>) {
        val client = SupabaseManager.getClient()
        tags.forEach { tag ->
            try {
                // Create an instance of TagAttachment for each tag
                val attachment = TagAttachment(listingID.toString(), tag.id)
                // Insert the attachment into the database
                client.postgrest["ListingTags"].insert(attachment)
            } catch (e: Exception) {
                Log.e("Supabase", "Error attaching tag ${tag.name} to listing: ${e.message}")
            }
        }
    }

    /**
     * Uploads an image to Supabase Storage.
     * @param imageByteArray The image data.
     * @param userID The ID of the user.
     * @return The URL of the uploaded image.
     */
    suspend fun uploadImageToSupabase(imageByteArray: ByteArray, userID: String): String {
        val bucket = SupabaseManager.getClient().storage["itemimages"]
        val imageName = "${userID}_${System.currentTimeMillis()}.jpeg"
        bucket.upload(imageName, imageByteArray, upsert = true)

        // Return the public URL of the uploaded image
        return imageName
    }
}