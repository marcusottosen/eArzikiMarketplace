package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.earzikimarketplace.R

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onClearClicked: () -> Unit,

    // Filter dropdown items
    expanded: MutableState<Boolean>,
    onSortSelected: (Int, Int) -> Unit,
    categoryID: Int
) {
    val height = 35.dp
    val fontSize = 14.sp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // Filter Icon Button
        Box(
            modifier = Modifier
                .width(55.dp)
                .height(height)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50) // Rounded corners
                )
        ) {
            IconButton(
                onClick = { onFilterClicked() },
                modifier = Modifier.fillMaxSize(), // Fill the available space in the Box
            ) {
                Image(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Filter",
                    modifier = Modifier
                        .size(25.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            FilterDropdown(
                expanded = expanded,
                onSortSelected = onSortSelected, // Pass the method reference
                categoryID = categoryID
            )
        }

        Spacer(modifier = Modifier.width(25.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.shapes.small
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(50)
                )
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                value = searchText,
                onValueChange = onSearchTextChanged,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    stringResource(R.string.search),
                                    style = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        fontSize = fontSize
                                    )
                                )
                            }
                            innerTextField()
                        }
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = onClearClicked) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.width(25.dp))

        // Search Icon Button
        Box(
            modifier = Modifier
                .width(55.dp)
                .height(height)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50) // Rounded corners
                )
        ) {
            IconButton(
                onClick = { onSearchClicked() },
                modifier = Modifier.fillMaxSize(), // Fill the available space in the Box
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Custom Icon",
                    modifier = Modifier
                        .size(25.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}