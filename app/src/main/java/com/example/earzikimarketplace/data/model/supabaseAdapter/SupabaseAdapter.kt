package com.example.earzikimarketplace.data.model.supabaseAdapter

import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.NavigationRoute
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.seconds


/**
 * Initializes the Supabase client.
 *
 * @param apiKey The Supabase API key. Stored locally
 * @param apiUrl The Supabase API URL. Stored locally
 */
object SupabaseManager {
    private var supabaseClient: SupabaseClient? = null
    private var goTrue: GoTrue? = null

    private lateinit var clientFactory: SupabaseClientFactory

    /**
     * Initializes the Supabase client with the provided API key and URL.
     * @param apiKey The Supabase API key.
     * @param apiUrl The Supabase API URL.
     * @param factory The factory used to create the Supabase client.
     */
    fun initializeClient(apiKey: String, apiUrl: String, factory: SupabaseClientFactory) {
        try {
            supabaseClient = factory.createSupabaseClient(apiKey, apiUrl)
            goTrue = supabaseClient?.gotrue
        } catch (e: Exception) {
            throw InitializationException("Supabase initialization failed", e)
        }
    }

    /**
     * Retrieves the Supabase client.
     * @return The Supabase client.
     * @throws SupabaseClientNotInitializedException if the Supabase client is not initialized.
     */
    fun getClient(): SupabaseClient {
        return supabaseClient
            ?: throw SupabaseClientNotInitializedException("Supabase client not initialized")
    }

    /**
     * Retrieves the GoTrue client used to handle users.
     * @return The GoTrue client.
     * @throws SupabaseClientNotInitializedException if the GoTrue client is not initialized.
     */
    fun getGoTrue(): GoTrue {
        return goTrue
            ?: throw SupabaseClientNotInitializedException("GoTrue client not initialized")
    }

    /**
     * Retrieves information about the logged-in user.
     * @return The user information.
     * @throws UserRetrievalException if the user information cannot be retrieved.
     */
    suspend fun getLoggedInUser(): UserInfo {
        try {
            return goTrue?.retrieveUserForCurrentSession(updateSession = true)
                ?: throw UserRetrievalException("User could not be retrieved")
        } catch (e: Exception) {
            throw UserRetrievalException("Failed to retrieve user", e)
        }
    }

    /**
     * Retrieves the current session so that the user stays logged in after app has been closed.
     * @return The current session.
     * @throws SessionRetrievalException if the session cannot be retrieved.
     */
    fun getSession(): String {
        try {
            return goTrue?.currentSessionOrNull().toString()    // Returns null if no session
        } catch (e: Exception) {
            throw SessionRetrievalException("Failed to retrieve session", e)
        }
    }

    /**
     * Signs out the current user.
     * @param navController The NavController for navigating after sign out.
     * @throws SessionRetrievalException if the sign out operation fails.
     */
    suspend fun signOut(navController: NavController) {
        try {
            goTrue?.logout()
            navController.navigate(NavigationRoute.Login.route)
        } catch (e: Exception) {
            throw SessionRetrievalException("Failed to sign out", e)
        }
    }

    /**
     * Refreshes the current session.
     * @return The refreshed session.
     * @throws SessionRetrievalException if the session cannot be refreshed.
     */
    suspend fun refreshSession(): String {
        try {
            return goTrue?.refreshCurrentSession().toString()
        } catch (e: Exception) {
            throw SessionRetrievalException("Failed to refresh session", e)
        }
    }
}

/**
 * Exception thrown when Supabase initialization fails.
 */
class InitializationException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when the Supabase client is not initialized.
 */
class SupabaseClientNotInitializedException(message: String) : IllegalStateException(message)

/**
 * Exception thrown when user information cannot be retrieved.
 */
class UserRetrievalException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when session retrieval fails.
 */
class SessionRetrievalException(message: String, cause: Throwable? = null) :
    Exception(message, cause)


/**
 * Factory interface for creating Supabase clients.
 */
interface SupabaseClientFactory {
    /**
     * Creates the Supabase client.
     * @param apiKey The Supabase API key.
     * @param apiUrl The Supabase API URL.
     * @return The Supabase client.
     */
    fun createSupabaseClient(apiKey: String, apiUrl: String): SupabaseClient
}

/**
 * Default implementation of SupabaseClientFactory.
 */
class DefaultSupabaseClientFactory : SupabaseClientFactory {
    override fun createSupabaseClient(apiKey: String, apiUrl: String): SupabaseClient {
        return createSupabaseClient(supabaseUrl = apiUrl, supabaseKey = apiKey) {
            install(Postgrest) {
                defaultSchema = "public"
                propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
            }
            install(Storage) {
                transferTimeout = 90.seconds
            }
            install(GoTrue) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
        }
    }
}

