package com.example.earzikimarketplace.data.util

import com.example.earzikimarketplace.R

sealed class NavigationRoute(var route: String) {
    object Marketplace : NavigationRoute("marketplace/{categoryID}") {
        fun createRoute(categoryID: Int) = "marketplace/$categoryID"
    }
    object AddItem                  : NavigationRoute("addItem")
    object ItemDetails              : NavigationRoute("itemDetails")
    object SignUp                   : NavigationRoute("signUp")
    object Login                    : NavigationRoute("login")
    object Home                     : NavigationRoute("Home")
    object AddItemStatusScreen      : NavigationRoute("addItemStatusScreen")
    object AddItemImagePicker       : NavigationRoute("addItemImagePicker")
    object SplashScreen             : NavigationRoute("splashScreen")
    object Profile                  : NavigationRoute("profile")

}

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("Home", R.drawable.home, "Home")
    object AddItem : NavigationItem("addItem", R.drawable.add_circle, "Sell")
    object Offers : NavigationItem("offers", R.drawable.storefront, "Offers")
    object Profile : NavigationItem("profile", R.drawable.person, "Profile")
}