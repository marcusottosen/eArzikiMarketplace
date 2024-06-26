package com.example.earzikimarketplace.ui.view.pages.addItem

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.CategoryEnum
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.view.reuseables.PageTop
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel
import com.example.earzikimarketplace.ui.viewmodel.MarketplaceViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItem(navController: NavController, viewModel: AddItemViewModel) {
    val context = LocalContext.current


    DisposableEffect(key1 = viewModel) {
        val listener = object : MarketplaceViewModel.MarketplaceListener {
            override fun onValidationSuccess() {
                navController.navigate(NavigationRoute.AddItemImagePicker.route)
            }

            override fun onItemAddedSuccess() {
                // Empty
            }

            override fun onError(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.listener = listener

        onDispose {
            viewModel.listener = null
        }
    }


    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("replace") }
    val priceState = remember { mutableStateOf("3999") }
    val isNumber = remember(priceState.value) { priceState.value.toFloatOrNull() != null }

    val categoryState =
        remember { mutableStateOf<CategoryEnum?>(CategoryEnum.HOME_CRAFTS) } // default = null
    val imageUrlsState = remember { mutableStateOf(listOf<String>()) }

    Scaffold(topBar = {
        PageTop(navController, stringResource(R.string.create_ad))
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {

                //Title
                OutlinedTextField(value = titleState.value,
                    onValueChange = { titleState.value = it },
                    label = { Text(stringResource(R.string.add_item_title)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Description
                OutlinedTextField(value = descriptionState.value,
                    onValueChange = { descriptionState.value = it },
                    label = { Text(stringResource(R.string.add_item_description)) },
                    maxLines = 7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .padding(bottom = 16.dp)
                )

                // Category
                // Dropdown for selecting category
                var expanded by remember { mutableStateOf(false) }
                val categories = CategoryEnum.values().map { it.getTitle(context) }

                OutlinedTextField(value = categoryState.value?.getTitle(context) ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.add_item_category)) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            Modifier.clickable { expanded = true })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(text = { Text(category) }, onClick = {
                            categoryState.value =
                                CategoryEnum.values().find { it.getTitle(context) == category }
                            expanded = false
                        })
                    }
                }

                OutlinedTextField(
                    value = priceState.value,
                    onValueChange = { priceState.value = it },
                    label = { Text(stringResource(R.string.add_item_price)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    leadingIcon = {
                        Text(
                            text = "CFA",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                    },
                )
                if (!isNumber && priceState.value.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.please_enter_a_valid_number),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // This spacer pushes the button to the bottom
            }

            Button(
                onClick = {
                    viewModel.checkAndAddListing(
                        context = context,
                        title = titleState.value,
                        description = descriptionState.value,
                        price = priceState.value,
                        categoryId = categoryState.value,
                        imageUrls = imageUrlsState.value
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f)
                    .widthIn(max = 800.dp)
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                shape = RoundedCornerShape(8.dp) // squared corners
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

