package com.example.earzikimarketplace.ui.view.pages.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()



    var email by remember { mutableStateOf(TextFieldValue("mkottosehhn@gmail.com")) }
    var password by remember { mutableStateOf(TextFieldValue("Marcus123")) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Page",)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, //color = MaterialTheme.colors.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sign-up button
        Button(
            onClick = {
                if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                    Log.d("LoginPage", "Login button clicked: $email, $password")
                    viewModel.login(email.text, password.text, context)
                } else {
                    errorMessage = "Please fill in all fields"
                }
            }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(NavigationRoute.SignUp.route)
            }
        ) {
            Text("Don't have an account? Sign up now!")
        }
    }

    // Observe the login state using the view model
    val loginState by viewModel.loginState.collectAsState()
    when (loginState) {
        is LoginViewModel.LoginState.Loading -> {
            CircularProgressIndicator()
        }
        is LoginViewModel.LoginState.Success -> {
            val email = (loginState as LoginViewModel.LoginState.Success).email
            Text("Sign-in successful! Email: $email")
            navController.navigate(NavigationRoute.NewHome.route)
            viewModel.resetLoginState()
        }
        is LoginViewModel.LoginState.Error -> {
            Text(text ="Error")
            var errorMessage = (loginState as LoginViewModel.LoginState.Error).message
            // Handle the login error here
            errorMessage = errorMessage
        }
        else -> {
            // Show the login form when the state is not loading, success, or error
        }
    }
}
