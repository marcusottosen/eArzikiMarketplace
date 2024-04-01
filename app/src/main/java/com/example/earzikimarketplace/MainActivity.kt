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
import androidx.activity.viewModels
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
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.data.util.getCurrentLocale
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
                    val sharedViewModel: SharedViewModel by viewModels {
                        SharedViewModel.provideFactory(context, application, startActivity = { intent ->
                            startActivity(intent)
                        })
                    }

                    val apiKey: String = BuildConfig.ApiKey;
                    val apiUrl: String = BuildConfig.ApiUrl;

                    SupabaseManager.initializeClient(apiKey, apiUrl)    // Initialize Supabase client

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    Scaffold(
                        bottomBar = {
                            if (currentRoute != NavigationRoute.Login.route &&
                                currentRoute != NavigationRoute.SignUp.route &&
                                currentRoute != NavigationRoute.SplashScreen.route
                                ) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) {
                        Navigation(navController = navController, sharedViewModel = sharedViewModel, context = context)
                    }
                }
            }
        }
    }
}


