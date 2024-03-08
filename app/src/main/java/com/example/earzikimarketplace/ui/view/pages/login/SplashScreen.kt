package com.example.earzikimarketplace.ui.view.pages.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel

@Composable
fun SplashScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.checkSession { isLoggedIn ->
            if (isLoggedIn) {
                navController.navigate(NavigationRoute.SplashScreen.route) {
                    popUpTo(NavigationRoute.SplashScreen.route) { inclusive = true }
                }
            } else {
                navController.navigate(NavigationRoute.Login.route) {
                    popUpTo(NavigationRoute.SplashScreen.route) { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator() // Show loading indicator
    }
}