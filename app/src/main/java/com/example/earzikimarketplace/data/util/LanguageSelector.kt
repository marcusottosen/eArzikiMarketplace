package com.example.earzikimarketplace.data.util

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.earzikimarketplace.R
import java.util.Locale

@Composable
fun LanguageSelector(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val supportedLanguages = listOf("en", "fr", "ha") // Add other languages as needed

    // Display current language
    Text(
        text = stringResource(R.string.current_language, currentLanguage),
        style = MaterialTheme.typography.bodyLarge,
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