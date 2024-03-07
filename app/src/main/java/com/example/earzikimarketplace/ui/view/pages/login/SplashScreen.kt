package com.example.earzikimarketplace.ui.view.pages.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    // This composable function will show a splash screen with your app logo
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Replace with your app logo or splash screen UI
        Text(text = "App Logo", fontSize = 24.sp)
    }
}