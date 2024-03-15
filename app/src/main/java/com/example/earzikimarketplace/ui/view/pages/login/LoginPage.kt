package com.example.earzikimarketplace.ui.view.pages.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField

import androidx.compose.runtime.*
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
import com.example.earzikimarketplace.data.util.getCurrentLocale
import com.example.earzikimarketplace.data.util.getLocalizedLanguageName
import com.example.earzikimarketplace.data.util.setLocale
import com.example.earzikimarketplace.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()



    var email by remember { mutableStateOf(TextFieldValue("mkottosehhn@gmail.com")) }
    var password by remember { mutableStateOf(TextFieldValue("Marcus123")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
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
        Button(
            onClick = {
                if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                    viewModel.login(email.text, password.text, context)
                } else {
                    errorMessage = context.getString(R.string.please_fill_in_all_fields)
                }
            }
        ) {
            Text(stringResource(R.string.login))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(NavigationRoute.SignUp.route)
            }
        ) {
            Text(stringResource(R.string.don_t_have_an_account_sign_up_now))
        }
        Spacer(modifier = Modifier.padding(bottom = 50.dp))

        val currentLanguage = getLocalizedLanguageName(getCurrentLocale(context)) // Get the current language
        LanguageSelector(
            currentLanguage = currentLanguage,
            onLanguageSelected = { newLanguage ->
                // Handle language selection
                setLocale(context, newLanguage) // Call setLocale with the new language code
            }
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
            viewModel.resetLoginState()
        }
        is LoginViewModel.LoginState.Error -> {
            Text(text = stringResource(R.string.error))
            var errorMessage = (loginState as LoginViewModel.LoginState.Error).message
            // Handle login error
            errorMessage = errorMessage
        }
        else -> {
        }
    }
}
