package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.example.earzikimarketplace.data.model.dataClass.SortOption
import kotlin.reflect.KFunction2

@Composable
fun FilterDropdown(
    expanded: MutableState<Boolean>,
    onSortSelected: KFunction2<Int, Int, Unit>,
    categoryID: Int
) {
    val sortOptions = SortOption.values()

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        sortOptions.forEach { option ->
            DropdownMenuItem(
                text = { Text(text = stringResource(id = option.resourceId)) },
                onClick = {
                    onSortSelected(option.id, categoryID)
                    expanded.value = false
                }
            )
        }
    }
}