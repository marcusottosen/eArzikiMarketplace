package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.earzikimarketplace.data.model.dataClass.SortOption

@Composable
fun FilterDropdown(
    expanded: MutableState<Boolean>,
    onSortSelected: (Int, Int) -> Unit,
    categoryID: Int
) {
    val sortOptions = SortOption.values()
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val colors = MaterialTheme.colorScheme

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.background(color = colors.background, shape = shapes.extraSmall)

    ) {
        sortOptions.forEach { option ->
            DropdownMenuItem(
                text = { Text(text = stringResource(id = option.resourceId),
                    style = typography.labelMedium.copy(color = colors.onSurface)
                )
                       },
                onClick = {
                    onSortSelected(option.id, categoryID)
                    expanded.value = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}