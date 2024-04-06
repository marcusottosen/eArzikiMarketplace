package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.BuildConfig
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.DefaultSupabaseClientFactory
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import com.example.earzikimarketplace.data.model.supabaseAdapter.UserRepository
import com.example.earzikimarketplace.data.model.supabaseAdapter.storeUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel responsible for managing user authentication logic preparing for model layer.
 */
class LoginViewModel() : ViewModel() {
    /**
     * State flow for sign up process
     */
    sealed class SignUpState {
        object Initial : SignUpState()
        object Loading : SignUpState()
        data class Success(val email: String) : SignUpState()
        data class Error(val message: String) : SignUpState()
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Initial)
    val signUpState: StateFlow<SignUpState> = _signUpState

    /**
     * Initiates the sign-up process.
     *
     * @param context The application context.
     * @param email The user's email.
     * @param firstname The user's first name.
     * @param surname The user's surname.
     * @param number The user's phone number.
     * @param age The user's age.
     * @param password The user's password.
     */
    fun signUp(
        context: Context,
        email: String,
        firstname: String,
        surname: String,
        number: Int,
        age: Int,
        password: String
    ) {
        _signUpState.value = SignUpState.Loading
        val repository = UserRepository()

        val userData = UserSignUp(
            user_id = null,
            email = email,
            firstname = firstname,
            surname = surname,
            location_id = UUID.fromString("4e54d6f1-26cf-43f6-9b8a-69e0ac2db74b"),  // default location
            profile_picture = null,
            phone_number = number,
            age = age,
            created_at = null
        )

        viewModelScope.launch {
            try {
                val user = repository.signUpUserAuth(
                    email,
                    password,
                    userData
                )     // Creates the user in db
                if (user != null) {
                    _signUpState.value = SignUpState.Success(email)
                    Log.d("LoginViewModel USER", "the users email is: ${user.email}")

                    try {
                        Log.d("LoginViewModel USER123", user.toString())

                        storeUserData(
                            context,
                            user
                        )    // Store user data in temporary local storage
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Error storing user data: ${e.message}")
                    }
                }
                _signUpState.value = SignUpState.Success(email)

                Log.d("USER viewmodel", "User email: ${user?.email.toString()}")

            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Error signing up: ${e.message}")
                _signUpState.value = SignUpState.Error("Error signing up: ${e.message}")
            }
        }
    }

    /**
     * Resets the signup and sign-in state.
     */
    fun resetStates() {
        _signUpState.value = SignUpState.Initial
        _loginState.value = LoginState.Initial
    }


    /**
     * State flow for login process
     */
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(val email: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Initiates the login process.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param context The application context.
     */
    fun login(email: String, password: String, context: Context) {
        val apiUrl: String = BuildConfig.ApiUrl
        val apiKey: String = BuildConfig.ApiKey
        val factory = DefaultSupabaseClientFactory()
        SupabaseManager.initializeClient(apiKey, apiUrl, factory)    // Initialize Supabase client

        val repository = UserRepository()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repository.loginUser(password, email, context)
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