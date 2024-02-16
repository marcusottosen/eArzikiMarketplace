package com.example.earzikimarketplace.ui.view.pages

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.model.dataClass.CategoryEnum
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.view.reuseables.PageTop
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel
import com.example.earzikimarketplace.ui.viewmodel.MarketplaceViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItem(navController: NavController, viewModel: AddItemViewModel) {
    val context = LocalContext.current
    //val viewModel: MarketplaceViewModel = viewModel()
    //val addItemStatus by viewModel.addItemStatus.collectAsState()


    // Toast manager
    viewModel.listener = object : MarketplaceViewModel.MarketplaceListener {
        override fun onItemAddedSuccess() {
            Toast.makeText(context, "Item added successfully!", Toast.LENGTH_SHORT).show()
        }

        override fun onError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val titleState = remember { mutableStateOf("title") }
    val descriptionState = remember { mutableStateOf("description") }
    val priceState = remember { mutableStateOf("123") }
    val isNumber = remember(priceState.value) { priceState.value.toFloatOrNull() != null }

    val categoryState = remember { mutableStateOf<CategoryEnum?>(CategoryEnum.HOME_CRAFTS) } // default = null
    val imageUrlsState = remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            PageTop(navController, "Create Ad")
        }
    ) { paddingValues ->
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
                OutlinedTextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Description
                OutlinedTextField(
                    value = descriptionState.value,
                    onValueChange = { descriptionState.value = it },
                    label = { Text("Description") },
                    maxLines = 7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)  // Set a minimum height
                        .padding(bottom = 16.dp)
                )

                // Category
                // Dropdown for selecting category
                var expanded by remember { mutableStateOf(false) }
                val categories = CategoryEnum.values().map { it.title }
                OutlinedTextField(
                    value = categoryState.value?.title ?: "",
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            Modifier.clickable { expanded = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                categoryState.value =
                                    CategoryEnum.values().find { it.title == category }
                                expanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = priceState.value,
                    onValueChange = { priceState.value = it },
                    label = { Text("Price") },
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
                        text = "Please enter a valid number",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // This spacer pushes the button to the bottom


            }

            Button(
                onClick = {
                    if (titleState.value.isBlank() || descriptionState.value.isBlank() || priceState.value.isBlank() || categoryState.value == null) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    // Convert price to float
                    val price = priceState.value.toFloatOrNull()
                    if (price == null) {
                        Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.prepareListing(
                        Listing(
                            title = titleState.value,
                            description = descriptionState.value,
                            price = price,
                            category_id = categoryState.value!!.id,
                            image_urls = imageUrlsState.value
                        )
                    )
                    //viewModel.addItem(newListing)
                    // Navigate to the status screen
                    navController.navigate(NavigationRoute.AddItemImagePicker.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f) // 80% of the screen width
                    .widthIn(max = 800.dp) // max width for large screens/tablets
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp) // more squared corners
            ) {
                Text("Next")
            }

        }


    }
}

/*Button(
           onClick = {
               if (titleState.value.isBlank() || descriptionState.value.isBlank() || priceState.value.isBlank() || categoryState.value == null) {
                   Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                   return@Button
               }

               // Convert price to float
               val price = priceState.value.toFloatOrNull()
               if (price == null) {
                   Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
                   return@Button
               }

               val newListing = Listing(
                   title = titleState.value,
                   description = descriptionState.value,
                   price = price,
                   category_id = categoryState.value!!.id,
                   image_urls = imageUrlsState.value
               )
               viewModel.addItem(context, newListing)
           },
           modifier = Modifier.align(Alignment.CenterHorizontally)
       ) {
           Text("Create Item")
       }*/


// Observing the addItemStatus
/*LaunchedEffect(addItemStatus) {
if (addItemStatus is AddItemStatus.Success) {
    navController.popBackStack()
    // Reset the status if needed
    viewModel.resetAddItemStatus()
}
}*/

