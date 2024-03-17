package com.example.earzikimarketplace.ui.view.pages.addItem

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.TagEnum
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.view.reuseables.PageTop
import com.example.earzikimarketplace.ui.viewmodel.AddItemViewModel
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckboxGrid(navController: NavController, viewModel: AddItemViewModel) {
    val context = LocalContext.current
    val columns = 2
    val rows = ceil(viewModel.allTags.size / columns.toFloat()).toInt()


    Scaffold(
        topBar = {
            PageTop(navController, stringResource(R.string.create_ad))
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (rowIndex in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (columnIndex in 0 until columns) {
                            val index = rowIndex * columns + columnIndex
                            if (index < viewModel.allTags.size) {
                                val tagItem = viewModel.allTags[index]
                                val isSelected = viewModel.selectedTags.contains(tagItem)

                                TagWithCircularCheckbox(
                                    tag = tagItem,
                                    isSelected = isSelected,
                                    onCheckedChange = { viewModel.toggleTagSelection(tagItem) }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f)) // Fill empty space for incomplete rows
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    if (viewModel.selectedTags.isEmpty()) {
                        Toast.makeText(
                            context,
                            R.string.please_pick_at_least_one_tag, Toast.LENGTH_SHORT
                        ).show()
                    } else
                        navController.navigate(NavigationRoute.AddItemImagePicker.route)
                },

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f) // 80% of the screen width
                    .widthIn(max = 800.dp) // max width for large screens/tablets
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }

}

@Composable
fun TagWithCircularCheckbox(tag: TagEnum, isSelected: Boolean, onCheckedChange: (Boolean) -> Unit) {
    // Fixed width for the checkbox and text container to ensure alignment
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp / 2 - 32.dp //  16dp padding on both sides

    Row(
        modifier = Modifier
            .padding(8.dp)
            .width(maxWidth)
            .clickable { onCheckedChange(!isSelected) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCheckbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = stringResource(id = tag.titleId),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}


@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (checked) MaterialTheme.colorScheme.primary else Color.White)
            .border(
                width = 2.dp,
                color = if (checked) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}