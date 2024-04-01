package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.earzikimarketplace.BuildConfig
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import com.example.earzikimarketplace.data.model.supabaseAdapter.UserRepository
import com.example.earzikimarketplace.data.model.supabaseAdapter.storeUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel() : ViewModel() {
    //val supabaseAdapter = SupabaseAdapter()

    sealed class SignUpState {
        object Initial : SignUpState()
        object Loading : SignUpState()
        data class Success(val email: String) : SignUpState()
        data class Error(val message: String) : SignUpState()
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Initial)
    val signUpState: StateFlow<SignUpState> = _signUpState


    fun signUp(context: Context, email: String, firstname: String, surname: String, number: Int, age: Int, password: String) {
        _signUpState.value = SignUpState.Loading

        //val apiKey = context.getString(R.string.API_TOKEN)
        //val apiUrl = context.getString(R.string.API_URL)
        //SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client

        val repository = UserRepository()

        val userData = UserSignUp(
            user_id = null,
            email = email,
            firstname = firstname,
            surname = surname,
            location_id = UUID.fromString("4e54d6f1-26cf-43f6-9b8a-69e0ac2db74b"),
            profile_picture = null,
            phone_number = number,
            age = age,
            created_at = null
        )

        viewModelScope.launch {
            try {
                val user = repository.signUpUserAuth(email, password, userData)     // Creates the user in db
                if (user != null) {
                    _signUpState.value = SignUpState.Success(email)
                    Log.d("LoginViewModel USER", "the users email is: ${user.email}")

                    try {
                        Log.d("LoginViewModel USER123", user.toString())

                        storeUserData(context, user)    // Store user data in temporary local storage
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Error storing user data: ${e.message}")
                    }

                    //val localUser = getLocalUserData(context)
                    //Log.d("LoginViewModel USER", "the local users email is: ${localUser.email}")

                    // Perform actions that should happen only after successful user retrieval
                    // Example: navigateToHomePage(navController, user)
                }
                // TODO: Validate?
                _signUpState.value = SignUpState.Success(email)

                Log.d("USER viewmodel", "User email: ${user?.email.toString()}")

            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Error signing up: ${e.message}")
                _signUpState.value = SignUpState.Error("Error signing up: ${e.message}")
            }
        }
    }


    fun resetLoginState() {
        _signUpState.value = SignUpState.Initial
        _loginState.value = LoginState.Initial
    }


    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(val email: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String, context: Context) {
        val apiUrl: String = BuildConfig.ApiUrl;
        val apiKey: String = BuildConfig.ApiKey;


        SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client

        val repository = UserRepository()

        val goTrue = SupabaseManager.getGoTrue()   // Get GoTrue instance


        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repository.loginUser(password, email, context)
                //val usertest = getLocalUser(apiKey, apiUrl)
                //Log.d("LoginViewModel", "usertest: $usertest")
                _loginState.value = LoginState.Success(email)

                Log.d("LoginViewModel1", response.toString())
                Log.d("LoginViewModel2", "responseee: $response")


            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Error logging in: ${e.message}")
                _loginState.value = LoginState.Error("Error logging in: ${e.message}")
            }
        }
    }
}

// TODO: Big changes to supabase client.
// Only updated for the login function, not the signup function or anywhere else in app.
// TODO: Fix the login function to work with the new supabase client
// It works by using the same client and gotrue for everything so that new ones dont get created every time one is needed.
// This way it is possible to retrieve the currently logged in user
// BUT FIRST TEST IF IT ACTUALLY WORKS RETRIEVING THE CURRENT USER FROM SOMEWHERE ELSE THAN LOGIN

//val goTrue = SupabaseManager.getGoTrue()   // Get GoTrue instance
//val client = SupabaseManager.getClient()   // Get SupabaseClient instance
//SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client (Should be done in the beginning of the app)


// TODO: Let login and sign up use the same states