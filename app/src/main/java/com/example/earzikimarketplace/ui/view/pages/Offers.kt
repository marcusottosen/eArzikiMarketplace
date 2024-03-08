package com.example.earzikimarketplace.ui.view.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.earzikimarketplace.ui.view.reuseables.OfferCard
import com.example.earzikimarketplace.ui.view.reuseables.PageTop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Offers(navController: NavController){
    Scaffold(
        topBar = {
            PageTop(navController, "Company Offers")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {

                    OfferCard(navController = navController)
                    OfferCard(navController = navController)
                    OfferCard(navController = navController)
                    OfferCard(navController = navController)
                    OfferCard(navController = navController)

                }
            }
        }
    }
}