package com.example.earzikimarketplace.data.util

sealed class NavigationRoute(var route: String) {
    object Marketplace : NavigationRoute("marketplace/{categoryID}") {
        fun createRoute(categoryID: Int) = "marketplace/$categoryID"
    }
    object AddItem                  : NavigationRoute("addItem")
    object ItemDetails              : NavigationRoute("itemDetails")
    object SignUp                   : NavigationRoute("signUp")
    object Login                    : NavigationRoute("login")
    object NewHome                    : NavigationRoute("newHome")
    object AddItemStatusScreen                    : NavigationRoute("addItemStatusScreen")
    object AddItemImagePicker                    : NavigationRoute("addItemImagePicker")

}