package com.example.earzikimarketplace

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import com.example.earzikimarketplace.data.util.Navigation
import com.example.earzikimarketplace.ui.theme.EArzikiMarketplaceTheme
import com.example.earzikimarketplace.ui.view.reuseables.BottomNavigationBar
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import java.util.Locale


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Retrieve the current context
            val context = LocalContext.current
            getCurrentLocale(context)

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

                    // Provide the startActivity lambda to the SharedViewModel
                    val sharedViewModel: SharedViewModel = viewModel(
                        factory = SharedViewModel.provideFactory(startActivity)
                    )
                    val apiKey = context.getString(R.string.API_TOKEN)
                    val apiUrl = context.getString(R.string.API_URL)
                    SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    Scaffold(
                        bottomBar = {
                            if (currentRoute != "login" && currentRoute != "splash") {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) {
                        Navigation(navController = navController, sharedViewModel = sharedViewModel, context = context)
                    }

                    //Navigation(navController = navController, sharedViewModel = sharedViewModel, context = context)
                }
            }
        }
    }
}
// TODO: Add loader that checks if user has signed in when opening the app
// Currently crashes when closing app and starts it up again.
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
