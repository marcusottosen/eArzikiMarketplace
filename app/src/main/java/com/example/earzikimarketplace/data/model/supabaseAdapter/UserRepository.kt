package com.example.earzikimarketplace.data.model.supabaseAdapter

import android.content.Context
import android.util.Log
import io.github.jan.supabase.postgrest.query.Columns
import com.example.earzikimarketplace.data.model.dataClass.Location
import com.example.earzikimarketplace.data.model.dataClass.User
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import java.util.UUID

class UserRepository() {

    suspend fun loginUser(password: String, email: String, context: Context): String{
        Log.d("UserRepository loginUser", "Attempting to log user in...")
        //val goTrue = getGoTrue(apiKey, apiUrl)
        val goTrue = SupabaseManager.getGoTrue()

        try {
            val response = goTrue.loginWith(Email){
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

    suspend fun signUpUserAuth(email: String, password: String, userData: UserSignUp): UserSignUp {
        val goTrue = SupabaseManager.getGoTrue()


        Log.d("New User", "Creating new user $email")
        // Sign up with Supabase auth
        try {
            val user = goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            Log.d("UserRepository", "SignUpWith response: $user")

            val userInfo = signUpUserTable(userData)
            Log.d("UserRepository", "userinfo response: $userInfo")

            //Log.d("USER", "User email: ${user.email.toString()}")

            Log.d("UserRepos", "returning user")
            Log.d("UserRepos", user.toString())
            return userInfo

        } catch (e: Exception) {
            Log.d("UserRepository", "Error signing up: $e")
            throw e // Rethrow the exception back
        }
    }


    private suspend fun signUpUserTable(userData_: UserSignUp): UserSignUp {
        try {
            val client = SupabaseManager.getClient()
            val goTrue = SupabaseManager.getGoTrue()
            val user = goTrue.retrieveUserForCurrentSession(updateSession = true)
            Log.d("SignUpUserTable", "User: $user")
            val userData = UserSignUp(
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
                Log.d("Posting", userData.toString())
                client.postgrest["users"].insert(userData)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error adding new item: ${e.message}")
                throw e // Rethrow the exception back
            }

            Log.d("SignUpUserTable", "User added successfully!")
            return userData

        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding new item: ${e.message}")
            throw e // Rethrow the exception back
        }
    }
}

suspend fun loadUser(userId: UUID): UserSignUp {
    val client = SupabaseManager.getClient()
    val response = client.postgrest["users"].select {
        eq("user_id", userId.toString())
    }
    Log.d("UserRepository", "response: $response")

    // Assuming decodeSingle returns a User or throws an exception if something goes wrong
    return response.decodeSingle<UserSignUp>()
}



// Stores the session token in shared preferences
fun storeSessionToken(token: String, context: Context) {
    val sharedPreferences = context.applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("session_token", token).apply()
}
//TODO: Needed? those below too

fun storeUserData(context: Context, userData: UserSignUp) {
    Log.d("UserRepository", "Storing user data for: $userData")

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

    Log.d("UserRepository", "in $sharedPreferences is stored firstname: ${sharedPreferences.getString("firstname", "not found")}")
}

fun getLocalUserData(context: Context): User {
    val sharedPreferences = context.getApplicationContext()
        .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "")!!
    val email = sharedPreferences.getString("email", "")!!
    val firstname = sharedPreferences.getString("firstname", "")!!
    val surname = sharedPreferences.getString("surname", "")!!
    val locationId = sharedPreferences.getInt("location_id", 0)
    val profilePicture = sharedPreferences.getInt("profile_picture", 0)
    val phoneNumber = sharedPreferences.getInt("phone_number", 0)
    val age = sharedPreferences.getInt("age", 18)

    Log.d("UserRepository", "Retrieving local user data for: $email in $sharedPreferences")

    return User(
        user_id = UUID.fromString(userId),
        email = email,
        firstname = firstname,
        surname = surname,
        location_id = locationId,
        profile_picture = profilePicture,
        phone_number = phoneNumber,
        age = age
    )
}

suspend fun getLocationData(locationID: UUID): Location{
    val client = SupabaseManager.getClient()
    Log.d("UserRepository1111", "fetching data for location ID: ${locationID}")

    val response = client.postgrest["locations"].select() {
        eq("location_id", locationID)
    }
    Log.d("UserRepository", "getLocationData response: $response")

    return response.decodeSingle<Location>()

}

suspend fun getLocationDataForUser(userId: UUID): Location {
    val client = SupabaseManager.getClient()
    // Step 1: Retrieve the user's location_id
    val userResponse = client.postgrest["users"]
        .select(columns = Columns.list("location_id")) {
            eq("user_id", userId.toString())
        }

    Log.d("UserRepository33333", "getUserLocationId response: $userResponse")

    val userLocationId = userResponse.decodeSingle<UserSignUp>().location_id
        ?: throw IllegalArgumentException("User location ID not found")

    Log.d("UserRepository44444", userLocationId.toString())

    // Step 2: Retrieve the location data using the location_id
    val locationResponse = client.postgrest["locations"]
        .select {
            eq("location_id", userLocationId.toString())
        }
    Log.d("UserRepository55555", "getLocationData response: $locationResponse")

    return locationResponse.decodeSingle<Location>()
}




// 13:12:49.060 UserRepository
// userinfo response:
// UserInfo(appMetadata=AppMetadata(provider=email, providers=[email]),
// aud=authenticated, confirmationSentAt=null,
// confirmedAt=2023-12-26T12:12:48.510979Z, createdAt=2023-12-26T12:12:48.507244Z,
// email=mkottouiksen@gmail.com,
// emailConfirmedAt=2023-12-26T12:12:48.510979Z,
// factors=[], id=103c822a-2782-4c42-8aa6-88154a67dda0,
// identities=[Identity(createdAt=2023-12-26T12:12:48.50972Z,
// id=103c822a-2782-4c42-8aa6-88154a67dda0,
// identityData={email="mkottouiksen@gmail.com",
// email_verified=false,
// phone_verified=false,
// sub="103c822a-2782-4c42-8aa6-88154a67dda0"},
// lastSignInAt=2023-12-26T12:12:48.509624Z,
// provider=email,
// updatedAt=2023-12-26T12:12:48.50972Z,
// userId=103c822a-2782-4c42-8aa6-88154a67dda0)],
// lastSignInAt=2023-12-26T12:12:48.513342Z,
// phone=,

// role=authenticated,
// updatedAt=2023-12-26T12:12:48.515085Z,
// userMetadata={}, phoneChangeSentAt=null,
// newPhone=null, emailChangeSentAt=null,
// newEmail=null, invitedAt=null,
// recoverySentAt=null, phoneConfirmedAt=null,
// actionLink=null)