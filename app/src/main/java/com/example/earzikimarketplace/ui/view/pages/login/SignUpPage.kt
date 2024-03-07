package com.example.earzikimarketplace.ui.view.pages.login

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current


    var email by remember { mutableStateOf("myEmail@gmail.com") }
    var password by remember { mutableStateOf("Marcus123") }
    var confirmPassword by remember { mutableStateOf("Marcus123") }
    var errorMessage by remember { mutableStateOf("") }

    var firstName by remember { mutableStateOf("John") }
    var surname by remember { mutableStateOf("Doe") }
    var phoneNumber by remember { mutableStateOf("12345678") }
    var age by remember { mutableStateOf("20") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = { /* Handle Done action if needed */ })
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = { /* Handle Done action if needed */ })
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Reset the error message at the beginning
                errorMessage = ""

                // Validate all fields
                when {
                    firstName.isEmpty() -> errorMessage = "First name is required"
                    surname.isEmpty() -> errorMessage = "Surname is required"
                    phoneNumber.length != 8 -> errorMessage = "Phone number must be 8 digits"
                    !phoneNumber.all { it.isDigit() } -> errorMessage = "Phone number must be numeric"
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errorMessage = "Invalid email format"
                    password.isEmpty() -> errorMessage = "Password is required"
                    confirmPassword.isEmpty() -> errorMessage = "Confirm password is required"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    firstName.any { it.isDigit() } -> errorMessage = "First name cannot contain numbers"
                    surname.any { it.isDigit() } -> errorMessage = "Surname cannot contain numbers"
                    age.isEmpty() -> errorMessage = "Age is required"
                    age.toIntOrNull()?.let { it < 16 } ?: true -> errorMessage = "You must be above 16 years old to sign up"

                }

                // Only proceed if there are no errors
                if (errorMessage.isEmpty()) {
                    viewModel.signUp(context, email, firstName, surname, phoneNumber.toInt(), age.toInt(), password)
                }
            }
        ) {
            Text("Sign Up")
        }

        TextButton(
            onClick = {
                navController.navigate(NavigationRoute.Login.route)
            }
        ) {
            Text("Already a member? Login!")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Observe the sign-up state using the view model
        val signUpState by viewModel.signUpState.collectAsState()
        when (signUpState) {
            is LoginViewModel.SignUpState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginViewModel.SignUpState.Success -> {
                val email = (signUpState as LoginViewModel.SignUpState.Success).email
                Text("Sign-up successful! Email: $email")
                navController.navigate(NavigationRoute.Home.route)
                viewModel.resetLoginState()
            }
            is LoginViewModel.SignUpState.Error -> {
                val rawErrorMessage = (signUpState as LoginViewModel.SignUpState.Error).message
                val userFriendlyErrorMessage = when {
                    rawErrorMessage.contains("user already registered", ignoreCase = true) -> "Email already registered"
                    rawErrorMessage.contains("not_found", ignoreCase = true) -> "Resource not found or access denied"
                    rawErrorMessage.contains("unauthorized", ignoreCase = true) -> "Unauthorized access"
                    rawErrorMessage.contains("too many requests", ignoreCase = true) -> "Too many requests, please try again later"
                    rawErrorMessage.contains("database_timeout", ignoreCase = true) -> "Database timeout, please try again later"
                    rawErrorMessage.contains("internal_server_error", ignoreCase = true) -> "Internal server error, please contact support"
                    else -> rawErrorMessage // Use the original error message if no specific condition is matched
                }
                Text(userFriendlyErrorMessage)
            }

            else -> {
                // Show the sign-up form when the state is not loading, success, or error
            }
        }
    }
}