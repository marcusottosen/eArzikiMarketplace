package com.example.earzikimarketplace.data.model.supabaseAdapter

import android.content.Context
import android.util.Log
import com.example.earzikimarket.data.model.dataClass.Listing
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import java.io.FileInputStream
import kotlin.time.Duration.Companion.seconds

/*
fun getGoTrue(key: String, url: String) : GoTrue {
    val client = getClient(url, key)
    return client.gotrue
}
fun getClient(url: String, key: String) : SupabaseClient{
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = url,
        supabaseKey = key
    ) {
        install(Postgrest){
            defaultSchema = "public"
            propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
        }
        install(GoTrue) {
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
    }
    return client
}
// TODO: Get rid of the above code and use the below code instead
*/
/**
 * Initializes the Supabase client.
 *
 * @param apiKey The Supabase API key. Stored locally
 * @param apiUrl The Supabase API URL. Stored locally
 */
object SupabaseManager {
    private var supabaseClient: SupabaseClient? = null
    private var goTrue: GoTrue? = null

    fun initializeClient(apiKey: String, apiUrl: String) {
        try {
            supabaseClient = createSupabaseClient(
                supabaseUrl = apiUrl,
                supabaseKey = apiKey
            ) {
                install(Postgrest){
                    defaultSchema = "public"
                    propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
                }
                install(Storage) {
                    transferTimeout = 90.seconds // Default: 120 seconds
                }
                install(GoTrue) {
                    alwaysAutoRefresh = true
                    autoLoadFromStorage = true
                }

            }
            goTrue = supabaseClient?.gotrue
        } catch (e: Exception) {
            // If fails, either no internet or invalid API key
            throw InitializationException("Supabase initialization failed", e)
        }
    }

    fun getClient(): SupabaseClient {
        return supabaseClient ?: throw SupabaseClientNotInitializedException("Supabase client not initialized")
    }

    fun getGoTrue(): GoTrue {
        return goTrue ?: throw SupabaseClientNotInitializedException("GoTrue client not initialized")
    }

    suspend fun getLoggedInUser(): UserInfo {
        try {
            return goTrue?.retrieveUserForCurrentSession(updateSession = true)
                ?: throw UserRetrievalException("User could not be retrieved")
        } catch (e: Exception) {
            throw UserRetrievalException("Failed to retrieve user", e)
        }
    }

    fun getSession(): String? {
        try {
            return goTrue?.currentSessionOrNull().toString()    // Returns null if no session
        } catch (e: Exception) {
            throw SessionRetrievalException("Failed to retrieve session", e)
        }
    }
    // TODO: Storing session token at UserRepository. Is it needed?
}
class InitializationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class SupabaseClientNotInitializedException(message: String) : IllegalStateException(message)
class UserRetrievalException(message: String, cause: Throwable? = null) : Exception(message, cause)
class SessionRetrievalException(message: String, cause: Throwable? = null) : Exception(message, cause)


//val client = SupabaseManager.getClient()   // Get SupabaseClient instance
//val goTrue = SupabaseManager.getGoTrue()   // Get GoTrue instance
//SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client (Should be done in the beginning of the app)


/*
class SupabaseAdapter () {
    private val tableName= "Listings"

    /*private val supabaseUrl = "https://waghuzctpmgexykusmhi.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndhZ2h1emN0cG1nZXh5a3VzbWhpIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY4Nzk0NDU2NCwiZXhwIjoyMDAzNTIwNTY0fQ.1UuyNzF1li_bntWP3qgG6wsWnhsJJ8jNICdjXF4tR18"

    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Postgrest){
            defaultSchema = "public"
            propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
        }
        install(GoTrue) {
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
    }
    //val gotrue = client.gotrue*/
    /*fun getGoTrue(key: String, url: String) : GoTrue {
        val client = getClient(url, key)
        return client.gotrue
    }*/


    /*private fun getClient(url: String, key: String) : SupabaseClient{
        val client: SupabaseClient = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Postgrest){
                defaultSchema = "public"
                propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
            }
            install(GoTrue) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
        }
        return client
    }*/



    // Gets a list of items within a category from the database
    suspend fun getItems(key: String, url: String, start: Long, pageSize: Long, category: Int): Result<List<Listing>> {
        return try {
            val client = getClient(url, key)

            Log.d("supabase", "getItems: $start, $pageSize")
            val response = client.postgrest[tableName].select {
                range(start until start + pageSize)
                order("post_date", Order.DESCENDING)  // Newest items first
                eq("active", "TRUE")
                eq("category_id", category)
            }
            val data = response.decodeList<Listing>()
            Log.d("supabase", "Data: $data")
            Result.success(data)
        } catch (e: Exception) {
            Log.e("supabase", "Error getting items: ${e.message}")
            Result.failure(e)
        }
    }

    // Gets the amount of items in a category
    suspend fun getCategoryCount(key: String, url: String, category: Int): Result<Int> {
        return try {
            Log.d("supabaseDEBUG", "getCategoryCount for ID: $category")

            val client = getClient(url, key)

            val response = client.postgrest[tableName].select {
                eq("active", "TRUE")
                eq("category_id", category)
            }
            Log.d("supabaseDEBUG", "response: $response")
            val data = response.decodeList<Listing>()
            Log.d("supabaseDEBUG", "size: ${data.size}")
            Result.success(data.size)
        } catch (e: Exception) {
            Log.e("supabaseDEBUG", "Error getting category count: ${e.message}")
            Result.failure(e)
        }
    }


    // Adds an item to the database
    suspend fun addItem(key: String, url: String, listing: Listing) {
        val client = getClient(url, key)

        try {
            Log.d("Posting", listing.toString())
            client.postgrest[tableName].insert(listing)
            Log.d("supabase", "Added new item: $listing")
        }catch (e: Exception){
            Log.e("supabase", "Error adding new item: ${e.message}")
            throw e // Rethrow the exception back
        }
    }
}
*/