package com.example.earzikimarketplace.data.util

import android.app.LocaleManager
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(languageCode)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.createConfigurationContext(config)
}

fun getCurrentLocale(context: Context): String {
    val currentAppLocales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales
    } else {
        AppCompatDelegate.getApplicationLocales()
    }
    Log.d(ContentValues.TAG, "Current App Locales: $currentAppLocales")
    return currentAppLocales.toString()
}