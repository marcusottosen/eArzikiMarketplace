package com.example.earzikimarketplace.ui.view.pages.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.util.LanguageSelector
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.LoginViewModel
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()


    var email by remember { mutableStateOf(TextFieldValue("testEmail@gmail.com")) }
    var password by remember { mutableStateOf(TextFieldValue("Test123!")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sign-up button
        Button(onClick = {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                viewModel.login(email.text, password.text, context)
            } else {
                errorMessage = context.getString(R.string.please_fill_in_all_fields)
            }
        }) {
            Text(stringResource(R.string.login))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate(NavigationRoute.SignUp.route)
        }) {
            Text(stringResource(R.string.don_t_have_an_account_sign_up_now))
        }
        Spacer(modifier = Modifier.padding(bottom = 50.dp))

        LanguageSelector(
            sharedViewModel = sharedViewModel, context = context
        )
    }

    // Observe the login state using the viewmodel
    val loginState by viewModel.loginState.collectAsState()
    when (loginState) {
        is LoginViewModel.LoginState.Loading -> {
            CircularProgressIndicator()
        }

        is LoginViewModel.LoginState.Success -> {
            val email = (loginState as LoginViewModel.LoginState.Success).email
            Text(stringResource(R.string.sign_in_successful_email, email))
            navController.navigate(NavigationRoute.Home.route)
            viewModel.resetStates()
        }

        is LoginViewModel.LoginState.Error -> {
            val errorMessage = (loginState as LoginViewModel.LoginState.Error).message
            // Handle login error
            if (errorMessage.contains("Invalid login credentials")) Text(stringResource(R.string.wrong_email_or_password))
            else Text(text = stringResource(R.string.error))
        }

        else -> {
            // Empty
        }
    }
}
