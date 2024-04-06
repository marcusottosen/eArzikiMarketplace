package com.example.earzikimarketplace.ui.view.pages.addItem

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.view.reuseables.FullScreenImageDialog
import com.example.earzikimarketplace.ui.view.reuseables.PageTop
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddItemImagePicker(navController: NavController, viewModel: AddItemViewModel) {
    val selectedImageUris by viewModel.selectedImageUris.observeAsState(emptyList())
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.addSelectedImageUri(it) }
        }
    val context = LocalContext.current

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
                Text(
                    text = stringResource(R.string.add_your_photos),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Image rows
                for (row in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (imageIndex in 0..1) {
                            val index = row * 2 + imageIndex
                            ImagePickerBox(
                                uri = selectedImageUris.getOrNull(index),
                                onPickImage = {
                                    if (selectedImageUris.size <= index) launcher.launch(
                                        "image/*"
                                    )
                                },
                                onRemoveClick = { viewModel.removeSelectedImageUri(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp)
                            )
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (viewModel.selectedImageUris.value.isNullOrEmpty()) {
                        Toast.makeText(
                            context, R.string.please_choose_at_least_one_image, Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    viewModel.uploadImagesAndAddItem(context)
                    navController.navigate(NavigationRoute.AddItemStatusScreen.route) {
                        popUpTo(NavigationRoute.AddItemStatusScreen.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f) // 80% of the screen width
                    .widthIn(max = 800.dp) // max width for large screens/tablets
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.post_item))
            }


        }
    }
}

@Composable
fun ImagePickerBox(
    uri: Uri?, onPickImage: () -> Unit, onRemoveClick: () -> Unit, modifier: Modifier = Modifier
) {
    var showFullScreen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (uri != null) Color.Transparent else Color.LightGray)
            .clickable { if (uri == null) onPickImage() else showFullScreen = true },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Remove image button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(40.dp) // Size of the outer Box
                    .padding(4.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f), // Semi-transparent black circle
                        shape = CircleShape
                    )
                    .clickable(onClick = onRemoveClick), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp) // Size of the Icon inside the Box
                )
            }

            if (showFullScreen) {
                FullScreenImageDialog(uri = uri) {
                    showFullScreen = false
                }
            }
        } else {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Icon")
        }
    }
}
