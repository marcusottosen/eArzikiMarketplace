package com.example.earzikimarketplace.ui.view.pages

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager.signOut
import com.example.earzikimarketplace.data.util.LanguageSelector
import com.example.earzikimarketplace.data.util.getCurrentLocale
import com.example.earzikimarketplace.data.util.getLocalizedLanguageName
import com.example.earzikimarketplace.data.util.setLocale
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel

@Composable
fun Profile(navController: NavController, sharedViewModel: SharedViewModel, context: Context) {
    var signOutTrigger by remember { mutableStateOf(false) }

    // Observe image loading preference
    val isImageLoadingEnabled by sharedViewModel.imageLoadingEnabled.observeAsState(initial = true)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.user_profile), style = MaterialTheme.typography.headlineMedium)


            Button(onClick = { navController.popBackStack() }) {
                Text(text = stringResource(R.string.back))
            }

            Spacer(modifier = Modifier.height(100.dp))

            val currentLanguage = getLocalizedLanguageName(getCurrentLocale(context)) // Get the current language

            LanguageSelector(
                currentLanguage = currentLanguage,
                onLanguageSelected = { newLanguage ->
                    // Handle language selection here
                    setLocale(context, newLanguage) // Call setLocale with the new language code
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.load_images_for_items_in_the_marketplace),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Switch(
                checked = isImageLoadingEnabled,
                onCheckedChange = {
                    sharedViewModel.toggleImageLoading()
                }
            )

            Spacer(modifier = Modifier.height(100.dp))

            Button(onClick = {
                // Trigger the sign out process
                signOutTrigger = true
            }) {
                Text(text = stringResource(R.string.log_out))
            }
        }
    }

    // LaunchedEffect to observe signOutTrigger state and run the sign-out process in a coroutine
    LaunchedEffect(signOutTrigger) {
        if (signOutTrigger) {
            signOut(navController)
            // Reset the trigger
            signOutTrigger = false
        }
    }
}