package com.example.earzikimarketplace.ui.view.pages.addItem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel
import kotlinx.coroutines.delay

@Composable
fun AddItemStatusScreen(navController: NavController, viewModel: AddItemViewModel) {

    val addItemStatus by viewModel.addItemStatus.collectAsState()

    // This LaunchedEffect is dedicated to the loading state
    /*LaunchedEffect(key1 = addItemStatus, key2 = viewModel) {
        if (addItemStatus is AddItemViewModel.AddItemStatus.Loading) {
            // Keep the loading icon for at least 2 seconds
            delay(4000)
            // After the delay, if the status is still Loading, update it to Success
            if (addItemStatus is AddItemViewModel.AddItemStatus.Loading) {
                viewModel.updateStatus(AddItemViewModel.AddItemStatus.Success)
            }
        }
    }*/

    // This LaunchedEffect is dedicated to the success state
    LaunchedEffect(key1 = addItemStatus) {
        if (addItemStatus is AddItemViewModel.AddItemStatus.Success) {
            // Keep the success icon for 2 seconds before navigating away
            delay(2000)
            navController.navigate(NavigationRoute.Home.route) {
                popUpTo(NavigationRoute.Home.route) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        when (addItemStatus) {
            is AddItemViewModel.AddItemStatus.Idle -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.preparing), color = Color.White)
                }
            }
            is AddItemViewModel.AddItemStatus.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp), color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.uploading), color = Color.White)
                }
            }
            is AddItemViewModel.AddItemStatus.Success -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.uploaded_successfully), color = Color.White)
                }
            }
            is AddItemViewModel.AddItemStatus.Error -> {
                Text(
                    text = "Failed: ${(addItemStatus as AddItemViewModel.AddItemStatus.Error).message}",
                    color = Color.Red
                )
            }
        }
    }
}

