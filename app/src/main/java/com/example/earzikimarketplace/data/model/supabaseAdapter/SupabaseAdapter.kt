package com.example.earzikimarketplace.data.model.supabaseAdapter

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.seconds


// TODO: Get rid of the above code and use the below code instead

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

