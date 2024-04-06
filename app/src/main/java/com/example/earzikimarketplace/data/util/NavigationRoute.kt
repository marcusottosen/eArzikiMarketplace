package com.example.earzikimarketplace.data.util

import com.example.earzikimarketplace.R

/**
 * Sealed class representing different navigation routes to pages.
 * @param route The route string associated with the navigation route.
 */
sealed class NavigationRoute(var route: String) {
    object Marketplace : NavigationRoute("marketplace/{categoryID}") {
        fun createRoute(categoryID: Int) = "marketplace/$categoryID"
    }

    object AddItem : NavigationRoute("addItem")
    object ItemDetails : NavigationRoute("itemDetails")
    object SignUp : NavigationRoute("signUp")
    object Login : NavigationRoute("login")
    object Home : NavigationRoute("Home")
    object AddItemStatusScreen : NavigationRoute("addItemStatusScreen")
    object AddItemImagePicker : NavigationRoute("addItemImagePicker")
    object SplashScreen : NavigationRoute("splashScreen")
    object Profile : NavigationRoute("profile")
    object Offers : NavigationRoute("offers")
    object CheckboxGrid : NavigationRoute("checkboxGrid")

}

/**
 * Sealed class used for bottom navigation.
 * @param route The route associated with the navigation item.
 * @param icon The icon resource ID associated with the navigation item.
 * @param title The title associated with the navigation item.
 */
sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("Home", R.drawable.home, "Home")
    object AddItem : NavigationItem("addItem", R.drawable.add_circle, "Sell")
    object Offers : NavigationItem("offers", R.drawable.storefront, "Offers")
    object Profile : NavigationItem("profile", R.drawable.person, "Profile")
}