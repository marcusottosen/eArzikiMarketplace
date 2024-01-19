package com.example.earzikimarketplace.data.util


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.earzikimarketplace.ui.view.pages.AddItem
import com.example.earzikimarketplace.ui.view.pages.AddItemImagePicker
import com.example.earzikimarketplace.ui.view.pages.AddItemStatusScreen
import com.example.earzikimarketplace.ui.view.pages.ItemInfoPage
import com.example.earzikimarketplace.ui.view.pages.MarketplaceScreen
import com.example.earzikimarketplace.ui.view.pages.Home
import com.example.earzikimarketplace.ui.view.pages.login.LoginPage
import com.example.earzikimarketplace.ui.view.pages.login.SignUpPage
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel

@ExperimentalFoundationApi
@Composable
fun Navigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val startingDestination = NavigationRoute.Login.route
    // NOTE! If changing the starting destination, remember to add "SupabaseManager.initializeClient(apiKey, apiUrl)"
// TODO: Retrieve current session, if none, then navigate to login page
    val addItemViewModel: AddItemViewModel = viewModel()   // Created here for shared states


    NavHost(
        navController,
        startDestination = startingDestination//NavigationRoute.NewHome.route //startingDestination
    ) {
        composable(NavigationRoute.SignUp.route) {
            SignUpPage(navController)
        }
        composable(NavigationRoute.Login.route) {
            LoginPage(navController = navController)
        }


       // composable(NavigationRoute.Marketplace.route) {
       //     //BackHandler(true) {}
       //     MarketplaceScreen(sharedViewModel, navController)
       // }

        composable(NavigationRoute.AddItem.route) {
            AddItem(navController, addItemViewModel)
        }
        composable(NavigationRoute.AddItemImagePicker.route) {
            AddItemImagePicker(navController, addItemViewModel)
        }
        composable(NavigationRoute.AddItemStatusScreen.route) {
            AddItemStatusScreen(navController, addItemViewModel)
        }

        composable(NavigationRoute.ItemDetails.route) {
            ItemInfoPage(sharedViewModel, navController)
        }

        composable(NavigationRoute.NewHome.route) {
            Home(navController)
        }

        composable(
            route = NavigationRoute.Marketplace.route,
            arguments = listOf(navArgument("categoryID") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryID = backStackEntry.arguments?.getInt("categoryID")
            if (categoryID != null) {
                MarketplaceScreen(sharedViewModel = sharedViewModel, navController = navController, categoryID = categoryID)
            }
        }


        /*
        composable(NavigationRoute.ItemDetails.route) {
            val jsonString = navController.previousBackStackEntry?.arguments?.getString("itemJson")
            Log.e("Navigation json", jsonString.toString())

            val itemModel = jsonString?.let { Json.decodeFromString(Item.serializer(), it) }
            Log.e("Navigation", itemModel.toString())

            if (itemModel != null) {
                ItemInfoPage(item = itemModel, navController = navController)
            } else
                AddItem(navController)
                Log.e("Nav to ItemDetails", "NULL ERROR")

        }*/

    }
}

/*itemModel?.let {
    Log.e("Navigation", "Should load ItemInfoPage 3")
    ItemInfoPage(item = it, navController = navController)
}*/

/*
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key)
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key)
}
*/