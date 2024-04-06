package com.example.earzikimarketplace.ui.view.pages.login

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var firstName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<Int?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = {})
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = {})
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(stringResource(R.string.first_name)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text(stringResource(R.string.surname)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(R.string.phone_number)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text(stringResource(R.string.age)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                // Reset the error message at the beginning
                errorMessage = null

                // Validate all fields
                when {
                    firstName.isEmpty() -> {
                        errorMessage = R.string.first_name_is_required
                    }

                    surname.isEmpty() -> {
                        errorMessage = R.string.surname_is_required
                    }

                    phoneNumber.length != 8 -> {
                        errorMessage = R.string.phone_number_must_be_8_digits
                    }

                    !phoneNumber.all { it.isDigit() } -> {
                        errorMessage = R.string.phone_number_must_be_numeric
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        errorMessage = R.string.invalid_email_format
                    }

                    password.isEmpty() -> {
                        errorMessage = R.string.password_is_required
                    }

                    confirmPassword.isEmpty() -> {
                        errorMessage = R.string.confirm_password_is_required
                    }

                    password != confirmPassword -> {
                        errorMessage = R.string.passwords_do_not_match
                    }

                    firstName.any { it.isDigit() } -> {
                        errorMessage = R.string.first_name_cannot_contain_numbers
                    }

                    surname.any { it.isDigit() } -> {
                        errorMessage = R.string.surname_cannot_contain_numbers
                    }

                    age.isEmpty() -> {
                        errorMessage = R.string.age_is_required
                    }

                    age.toIntOrNull()?.let { it < 16 } ?: true -> {
                        errorMessage =
                            R.string.you_must_be_above_16_years_old_to_sign_up
                    }
                }

                // Only proceed if there are no errors
                if (errorMessage == null) {
                    viewModel.signUp(
                        context,
                        email,
                        firstName,
                        surname,
                        phoneNumber.toInt(),
                        age.toInt(),
                        password
                    )
                }
            }
        ) {
            Text(stringResource(R.string.sign_up))
        }

        TextButton(
            onClick = {
                navController.navigate(NavigationRoute.Login.route)
            }
        ) {
            Text(stringResource(R.string.already_a_member_login))
        }

        errorMessage?.let { resId ->
            Text(stringResource(id = resId), color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Observe the sign-up state using the view model
        val signUpState by viewModel.signUpState.collectAsState()
        when (signUpState) {
            is LoginViewModel.SignUpState.Loading -> {
                CircularProgressIndicator()
            }

            is LoginViewModel.SignUpState.Success -> {
                val email = (signUpState as LoginViewModel.SignUpState.Success).email
                Text(stringResource(R.string.sign_up_successful_email, email))
                navController.navigate(NavigationRoute.Home.route)
                viewModel.resetStates()
            }

            is LoginViewModel.SignUpState.Error -> {
                val rawErrorMessage = (signUpState as LoginViewModel.SignUpState.Error).message
                val userFriendlyErrorMessage = when {
                    rawErrorMessage.contains("user already registered", ignoreCase = true) -> {
                        stringResource(R.string.email_already_registered)
                    }

                    rawErrorMessage.contains("not_found", ignoreCase = true) -> {
                        stringResource(R.string.resource_not_found_or_access_denied)
                    }

                    rawErrorMessage.contains("unauthorized", ignoreCase = true) -> {
                        stringResource(R.string.unauthorized_access)
                    }

                    rawErrorMessage.contains("too many requests", ignoreCase = true) -> {
                        stringResource(R.string.too_many_requests_please_try_again_later)
                    }

                    rawErrorMessage.contains("database_timeout", ignoreCase = true) -> {
                        stringResource(R.string.database_timeout_please_try_again_later)
                    }

                    rawErrorMessage.contains("internal_server_error", ignoreCase = true) -> {
                        stringResource(R.string.internal_server_error_please_contact_support)
                    }

                    else -> {
                        rawErrorMessage
                    } // Use the original error message if no specific condition is matched
                }
                Text(userFriendlyErrorMessage)
            }

            else -> {
            }
        }
    }
}