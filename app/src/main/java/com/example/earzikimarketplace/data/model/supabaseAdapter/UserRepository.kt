package com.example.earzikimarketplace.data.model.supabaseAdapter

import android.content.Context
import android.util.Log
import com.example.earzikimarketplace.data.model.dataClass.Location
import com.example.earzikimarketplace.data.model.dataClass.User
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import java.util.UUID

class UserRepository() {
    /**
     * Logs in a user with the provided email and password.
     * @param password The user's password.
     * @param email The user's email.
     * @param context The application context.
     * @return The response after logging in.
     * @throws Exception if an error occurs during login.
     */
    suspend fun loginUser(password: String, email: String, context: Context): String {
        val goTrue = SupabaseManager.getGoTrue()

        try {
            val response = goTrue.loginWith(Email) {
                this.email = email
                this.password = password
            }
            val session = SupabaseManager.getSession()
            if (session != null) {
                storeSessionToken(session.toString(), context)
                Log.d("UserRepository loginUser", "current user: $session")
            }

            return response.toString()

        } catch (e: Exception) {
            Log.e("SignUpViewModel", "Error logging in: ${e.message}")
            throw e
        }
    }

    /**
     * Signs up a user with the provided email, password, and user data.
     * @param email The user's email.
     * @param password The user's password.
     * @param userData The user's additional data for sign up.
     * @return The signed-up user's data.
     * @throws Exception if an error occurs during sign-up.
     */
    suspend fun signUpUserAuth(email: String, password: String, userData: User): User {
        val goTrue = SupabaseManager.getGoTrue()

        Log.d("New User", "Creating new user $email")
        // Sign up with Supabase auth
        try {
            val user = goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userInfo = signUpUserTable(userData)
            return userInfo

        } catch (e: Exception) {
            Log.d("UserRepository", "Error signing up: $e")
            throw e // Rethrow the exception back
        }
    }


    /**
     * Signs up the user in auth and then saves the userdata in the user table
     * @param userData_ The user's additional data for sign up.
     * @return The signed-up user's data.
     * @throws Exception if an error occurs during sign-up.
     */
    private suspend fun signUpUserTable(userData_: User): User {
        try {
            val client = SupabaseManager.getClient()
            val goTrue = SupabaseManager.getGoTrue()
            val user = goTrue.retrieveUserForCurrentSession(updateSession = true)
            val userData = User(
                user_id = UUID.fromString(user.id),
                email = user.email,
                firstname = userData_.firstname,
                surname = userData_.surname,
                location_id = userData_.location_id,
                profile_picture = null,
                phone_number = userData_.phone_number,
                age = userData_.age,
                created_at = user.createdAt
            )

            try {
                client.postgrest["users"].insert(userData)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error adding new item: ${e.message}")
                throw e // Rethrow the exception back
            }

            return userData

        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding new item: ${e.message}")
            throw e // Rethrow the exception back
        }
    }
}

/**
 * Loads user data from the database based on the user's ID.
 * @param userId The ID of the user.
 * @return The user's data.
 */
suspend fun loadUser(userId: UUID): User {
    val client = SupabaseManager.getClient()
    val response = client.postgrest["users"].select {
        eq("user_id", userId.toString())
    }
    Log.d("UserRepository", "response: $response")

    return response.decodeSingle<User>()
}

/**
 * Stores the session token in shared preferences
 * @param token the token the session is stored under (userID)
 * @param context The application context.
 */
fun storeSessionToken(token: String, context: Context) {
    val sharedPreferences =
        context.applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("session_token", token).apply()
}

/**
 * Stores user data in shared preferences.
 * @param context The application context.
 * @param userData The user's data to be stored.
 */
fun storeUserData(context: Context, userData: User) {

    val sharedPreferences = context.getApplicationContext()
        .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putString("userId", userData.user_id.toString())
        putString("email", userData.email.toString())
        putString("firstname", userData.firstname)
        putString("surname", userData.surname)
        putString("location_id", userData.location_id?.toString())
        putInt("profile_picture", userData.profile_picture ?: 0)
        putInt("phone_number", userData.phone_number ?: 11111111)
        putInt("age", userData.age ?: 0)
        apply()
    }
}

/**
 * Retrieves location data from the database based on the location ID.
 * @param locationID The ID of the location.
 * @return The location data.
 */
suspend fun getLocationData(locationID: UUID): Location {
    val client = SupabaseManager.getClient()

    val response = client.postgrest["locations"].select() {
        eq("location_id", locationID)
    }
    return response.decodeSingle<Location>()

}