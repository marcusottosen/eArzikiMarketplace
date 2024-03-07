package com.example.earzikimarketplace.ui.view.pages

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager.signOut
import com.example.earzikimarketplace.getCurrentLocale
import com.example.earzikimarketplace.setLocale
import java.util.Locale

@Composable
fun Profile(navController: NavController, context: Context) {
    var signOutTrigger by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "User Profile", fontSize = 24.sp)

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }

            Spacer(modifier = Modifier.height(30.dp))

            val supportedLanguages = listOf("en", "fr", "ha") // Add other languages as needed
            val currentLanguage = getLocalizedLanguageName(getCurrentLocale(context)) // Get the current language from your existing method

            LanguageSelector(
                supportedLanguages = supportedLanguages,
                currentLanguage = currentLanguage,
                onLanguageSelected = { newLanguage ->
                    // Handle language selection here
                    setLocale(context, newLanguage) // Call setLocale with the new language code
                }
            )

            Spacer(modifier = Modifier.height(200.dp))

            Button(onClick = {
                // Trigger the sign out process
                signOutTrigger = true
            }) {
                Text(text = "Log Out")
            }
        }
    }

    // LaunchedEffect to observe signOutTrigger state and run the sign-out process in a coroutine
    LaunchedEffect(signOutTrigger) {
        if (signOutTrigger) {
            signOut(navController)
            // Reset the trigger
            signOutTrigger = false
        }
    }
}

@Composable
fun LanguageSelector(
    supportedLanguages: List<String>,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    // Display current language
    Text(
        text = "Current Language: $currentLanguage",
        modifier = Modifier.padding(8.dp)
    )

    // Row of language buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        supportedLanguages.forEach { language ->
            if (language != currentLanguage) {
                Button(
                    onClick = { onLanguageSelected(language) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = getLocalizedLanguageName(language))
                }
            }
        }
    }
}

fun getLocalizedLanguageName(languageCode_: String): String {
    val languageCode = languageCode_.replace("[", "").replace("]", "")
    return try {
        val locale = Locale(languageCode)
        val localized = locale.getDisplayLanguage(locale)
        if (localized.isEmpty()) languageCode else localized
    } catch (e: Exception) {
        languageCode
    }
}