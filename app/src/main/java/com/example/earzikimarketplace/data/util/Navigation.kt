package com.example.earzikimarketplace.data.util

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager.getSession
import com.example.earzikimarketplace.ui.view.pages.Home
import com.example.earzikimarketplace.ui.view.pages.ItemInfoPage
import com.example.earzikimarketplace.ui.view.pages.MarketplaceScreen
import com.example.earzikimarketplace.ui.view.pages.Offers
import com.example.earzikimarketplace.ui.view.pages.Profile
import com.example.earzikimarketplace.ui.view.pages.addItem.AddItem
import com.example.earzikimarketplace.ui.view.pages.addItem.AddItemImagePicker
import com.example.earzikimarketplace.ui.view.pages.addItem.AddItemStatusScreen
import com.example.earzikimarketplace.ui.view.pages.addItem.CheckboxGrid
import com.example.earzikimarketplace.ui.view.pages.login.LoginPage
import com.example.earzikimarketplace.ui.view.pages.login.SignUpPage
import com.example.earzikimarketplace.ui.view.pages.login.SplashScreen
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel

/**
 * Composable function for handling navigation within the app.
 * @param navController The NavController used for navigating between destinations.
 * @param sharedViewModel The SharedViewModel used for managing shared states between composables.
 * @param context The context used for accessing resources and system information.
 */
@ExperimentalFoundationApi
@Composable
fun Navigation(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    context: Context
) {
    LaunchedEffect(key1 = "refreshSession") {  // Runs only once
        try {
            val session = getSession()
            //Log.d("Navigation", "Session: $session")

            // Navigate based on valid session
            val destination = if (session.isEmpty() || session == "null") {
                //Log.d("Navigation", "Session is invalid")
                NavigationRoute.Login.route
            } else {
                //Log.d("Navigation", "Session is valid")
                NavigationRoute.Home.route
            }

            // Perform navigation to either home or login
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        } catch (e: Exception) {
            Log.e("Navigation", "Error refreshing session: $e")
            NavigationRoute.Login.route
        }
    }

    val addItemViewModel: AddItemViewModel = viewModel()   // Created here for shared states

    NavHost(
        navController,
        startDestination = NavigationRoute.SplashScreen.route
    ) {
        // Sign-up/sign-in pages
        composable(NavigationRoute.SignUp.route) {
            SignUpPage(navController)
        }
        composable(NavigationRoute.Login.route) {
            LoginPage(navController = navController, sharedViewModel = sharedViewModel)
        }

        composable(NavigationRoute.SplashScreen.route) {
            SplashScreen(navController = navController, sharedViewModel = sharedViewModel)
        }

        // Add item pages
        composable(NavigationRoute.AddItem.route) {
            AddItem(navController, addItemViewModel)
        }
        composable(NavigationRoute.AddItemImagePicker.route) {
            AddItemImagePicker(navController, addItemViewModel)
        }
        composable(NavigationRoute.AddItemStatusScreen.route) {
            AddItemStatusScreen(navController, addItemViewModel)
        }
        composable(NavigationRoute.CheckboxGrid.route) {
            CheckboxGrid(navController, addItemViewModel)
        }



        composable(NavigationRoute.ItemDetails.route) {
            ItemInfoPage(sharedViewModel, navController)
        }
        composable(NavigationRoute.Home.route) {
            Home(navController)
        }
        composable(NavigationRoute.Profile.route) {
            Profile(navController, sharedViewModel, context)
        }
        composable(NavigationRoute.Offers.route) {
            Offers(navController)
        }
        composable(
            route = NavigationRoute.Marketplace.route,
            arguments = listOf(navArgument("categoryID") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryID = backStackEntry.arguments?.getInt("categoryID")
            if (categoryID != null) {
                MarketplaceScreen(
                    sharedViewModel = sharedViewModel,
                    navController = navController,
                    pageCategoryID = categoryID
                )
            }
        }


    }
}


