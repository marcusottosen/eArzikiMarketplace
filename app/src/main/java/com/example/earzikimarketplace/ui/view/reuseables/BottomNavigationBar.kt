package com.example.earzikimarketplace.ui.view.reuseables

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.util.NavigationItem
import com.example.earzikimarketplace.data.util.NavigationRoute

/**
 * NavBar inspiration from https://github.com/johncodeos-blog/BottomNavigationBarComposeExample
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.AddItem,
        NavigationItem.Offers,
        NavigationItem.Profile
    )
    NavigationBar(
        containerColor = colorResource(id = R.color.white), tonalElevation = 12.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Allow for others pages to keep the nav item "active"
        val isHomeActive =
            currentRoute == NavigationItem.Home.route || currentRoute?.startsWith(NavigationRoute.Marketplace.route) == true || currentRoute == NavigationRoute.ItemDetails.route
        Log.d("bottomNavBar", isHomeActive.toString())
        items.forEach { item ->
            val isSelected = when (item) {
                NavigationItem.Home -> isHomeActive
                else -> currentRoute == item.route
            }
            NavigationBarItem(icon = {
                val iconColor =
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    )
                Icon(
                    painterResource(id = item.icon),
                    contentDescription = item.title,
                    modifier = Modifier.size(25.dp),
                    tint = iconColor
                )
            }, label = {
                Text(
                    text = item.title, style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },

                alwaysShowLabel = true, selected = false, onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = false
                    }
                })
        }
    }
}