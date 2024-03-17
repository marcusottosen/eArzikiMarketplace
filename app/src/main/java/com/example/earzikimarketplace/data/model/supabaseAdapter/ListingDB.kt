package com.example.earzikimarketplace.data.model.supabaseAdapter

import android.util.Log
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
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.UUID

class ListingsDB () {
    private val tableName = "Listings"

    /**
     * Gets a list of items within a category from the database
     */
    suspend fun getItems(
        start: Long,
        pageSize: Long,
        category: Int
    ): Result<List<Listing>> {

        return try {
            //val client = getClient(url, key)
            val client = SupabaseManager.getClient()

            //Log.d("supabase", "getItems: $start, $pageSize")
            val response = client.postgrest[tableName].select {
                range(start until start + pageSize)
                order("post_date", Order.DESCENDING)  // Newest items first
                eq("active", "TRUE")
                eq("category_id", category)
            }
            val data = response.decodeList<Listing>()
            //Log.d("supabase", "Data: $data")
            Result.success(data)
        } catch (e: Exception) {
            Log.e("supabase", "Error getting items: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Gets a list of items within a category with a specific tag from the database
     */
    suspend fun getItemsByTagId(
        start: Long,
        pageSize: Long,
        category: Int,
        tagId: Int
    ): Result<List<Listing>> {

        return try {
            val client = SupabaseManager.getClient()

            val columns = Columns.raw("*, ListingTags!inner(tag_id)")
            Log.d("supabase ListingDB", "category: $category")
            Log.d("supabase ListingDB", "TagId: $tagId")


            val response = client.postgrest["Listings"].select(columns = columns) {
                range(start until start + pageSize)
                order("post_date", Order.DESCENDING)  // Newest items first
                eq("active", "TRUE")
                eq("category_id", category)
                filter(column = "ListingTags.tag_id", operator = FilterOperator.EQ, value = tagId)
            }

            // Decode the response into a list of Listing objects
            val data = response.decodeList<Listing>()
            Log.d("supabase data", "Items: $data")

            Result.success(data)
        } catch (e: Exception) {
            // Log the error and return a failure result
            Log.e("supabase", "Error getting items by tag_id: ${e.message}")
            Result.failure(e)
        }
    }



    suspend fun searchListingsByTitle(
        //start: Long,
        //pageSize: Long,
        //category: Int,
        searchQuery: String
    ): Result<List<Listing>> {
        return try {
            val client = SupabaseManager.getClient()
            Log.d("supabase", "searchListingsByTitle: $searchQuery")

            /*val formattedQuery = searchQuery.split(" ").joinToString(" & ") // Format the query for full-text search


            val response = client.postgrest[tableName].select {
                eq("active", "TRUE")
                eq("category_id", category)
                filter("title", FilterOperator.FTS, "title")

                range(start until start + pageSize)
                order("post_date", Order.DESCENDING)  // Newest items first
            }*/

            val response = client.postgrest["users"].select {
                textSearch(column = "firstname", query = "my", textSearchType = TextSearchType.PLAINTO)
            }

            val response2 = client.postgrest["users"].select(columns = Columns.list("email")) {
                textSearch(column = "email", query = "'email'", config = "english", textSearchType = TextSearchType.WEBSEARCH)
            }
            Log.d("supabase", "response: $response")

            //val response = client.postgrest["Listings"].select(columns = Columns.list("title")) {
            //    textSearch(column = "title", query = "title", config = "english", textSearchType = TextSearchType.PLAINTO)
            //}

            val data = response.decodeList<Listing>()
            Log.d("supabase", "Data: $data")
            Result.success(data)
        } catch (e: Exception) {
            Log.e("supabase", "Error searching listings by title: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getItemImage(url: String): ByteArray {
        val client = SupabaseManager.getClient()
        val bucket = client.storage["itemimages"]
        return bucket.downloadAuthenticated(url)
    }



//             val columns = Columns.raw("*, ListingTags!inner(tag_id)")

    // Gets the amount of items in a category
    /**
     * Gets the amount of items in a category by retrieving "active" column for the specific category and counting them.
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
            //Log.d("supabaseDEBUG reseponse", "response: $response")
            ///val data = response.decodeList<Listing>()
            val data = response.decodeList<ActiveRecord>()

            //Log.d("supabaseDEBUG size", "size: ${data.size}")
            Result.success(data.size)
        } catch (e: Exception) {
            Log.e("supabaseDEBUG", "Error getting category count: ${e.message}")
            Result.failure(e)
        }
    }
    @Serializable
    data class ActiveRecord(val active: Boolean)


    // Adds an item to the database
    suspend fun addItem(
        listing: Listing
    ): UUID {
        val client = SupabaseManager.getClient()
        try {
            Log.d("Posting", listing.toString())
            client.postgrest[tableName].insert(listing)
            Log.d("supabase", "Added new item: $listing")
            return listing.listing_id
        } catch (e: Exception) {
            Log.e("supabase", "Error adding new item: ${e.message}")
            throw e // Rethrow the exception back
        }
    }

    suspend fun attachTags(listingID: UUID, tags: List<TagEnum>){
        val client = SupabaseManager.getClient()
        tags.forEach { tag ->
            try {
                // Create an instance of TagAttachment for each tag
                val attachment = TagAttachment(listingID.toString(), tag.id)
                // Insert the attachment into the database
                client.postgrest["ListingTags"].insert(attachment)
                Log.d("Supabase", "Attached tag ${tag.name} to listing $listingID")
            } catch (e: Exception) {
                Log.e("Supabase", "Error attaching tag ${tag.name} to listing: ${e.message}")
            }
        }
    }

    // Function to upload image to Supabase Storage
    suspend fun uploadImageToSupabase(imageByteArray: ByteArray, userID: String): String {
        val bucket = SupabaseManager.getClient().storage["itemimages"]
        val imageName = "${userID}_${System.currentTimeMillis()}.jpeg"
        Log.d("ListingsDB", "uploadImageToSupabase name: $imageName")
        bucket.upload(imageName, imageByteArray, upsert = true)

        // Return the public URL of the uploaded image
        return imageName
    }
}