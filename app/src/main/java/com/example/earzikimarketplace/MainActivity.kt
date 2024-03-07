package com.example.earzikimarketplace

import android.app.LocaleManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.earzikimarket.R
import com.example.earzikimarketplace.data.util.Navigation
import com.example.earzikimarketplace.ui.theme.EArzikiMarketplaceTheme
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import com.example.earzikimarketplace.ui.theme.EArzikiMarketplaceTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Retrieve the current context
            val context = LocalContext.current
            getCurrentLocale(context)
            //setLocale(context, "en")


            // Define the startActivity lambda
            val startActivity: (Intent) -> Unit = { intent ->
                context.startActivity(intent)
            }

            EArzikiMarketplaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    //val sharedViewModel: SharedViewModel = viewModel()

                    // Provide the startActivity lambda to the SharedViewModel
                    val sharedViewModel: SharedViewModel = viewModel(
                        factory = SharedViewModel.provideFactory(startActivity)
                    )


                    Navigation(navController = navController, sharedViewModel = sharedViewModel)
                }
            }
        }
    }
}

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
