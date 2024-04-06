package com.example.earzikimarketplace.data.util

import android.content.Context
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
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import java.util.Locale

@Composable
fun LanguageSelector(
    sharedViewModel: SharedViewModel,
    context: Context
) {
    val currentLanguage = getLocalizedLanguageName(getCurrentLocale(context)) // Get the current language

    val supportedLanguages = listOf("en", "fr", "ha")

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
                    onClick = {
                        //onLanguageSelected(language)
                        setLocale(context, language)
                        sharedViewModel.updateLanguage()
                              },
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

fun getLanguageLocaleString(context: Context): Locale {
    val currentLocale = getCurrentLocale(context).removeSurrounding("[", "]")
    val localeParts = currentLocale.split("-")
    val newLocale = if (localeParts.size > 1) {
        Locale(localeParts[0], localeParts[1]) // Use language and country constructor
    } else {
        Locale(currentLocale) // Use single language code constructor
    }
    return newLocale
}