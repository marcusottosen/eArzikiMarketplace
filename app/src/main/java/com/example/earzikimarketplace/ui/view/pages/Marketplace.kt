package com.example.earzikimarketplace.ui.view.pages

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.CategoryEnum
import com.example.earzikimarketplace.data.model.dataClass.TagEnum
import com.example.earzikimarketplace.data.model.dataClass.UiState
import com.example.earzikimarketplace.ui.view.reuseables.FilterDropdown
import com.example.earzikimarketplace.ui.view.reuseables.ItemCard
import com.example.earzikimarketplace.ui.view.reuseables.SearchBar
import com.example.earzikimarketplace.ui.viewmodel.MarketplaceViewModel
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(sharedViewModel: SharedViewModel, navController: NavController, pageCategoryID: Int) {
    val context = LocalContext.current
    val viewModel: MarketplaceViewModel = viewModel()

    // States and items
    val items by viewModel.items.observeAsState(initial = emptyList())
    val uiState by viewModel.uiState.observeAsState(UiState.LOADING)
    val isPaginating by viewModel.isPaginating.observeAsState(false)

    val clickedTag = remember { mutableStateOf(-1) }

    // Search
    var searchText by remember { mutableStateOf("") }
    val onSearchClicked = {
        Log.d("Marketplace", "Search button clicked: $searchText")
        viewModel.searchItems(searchText)
    }

    // Handle the filter button
    val showFilterMenu  = remember { mutableStateOf(false) }
    val onFilterClicked = {
        Log.d("Marketplace", "Filter button clicked")
        showFilterMenu .value = !showFilterMenu .value // Toggle visibility
    }

    // Gets activated when the clear button is clicked
    val onClearSearchClicked = {
        Log.d("Marketplace", "Clear button clicked")
        searchText = ""
    }

    val onTagClicked: (Int) -> Unit = { categoryID ->
        Log.d("Marketplace", "Category button clicked: $categoryID")
        clickedTag.value = categoryID
    }

    // Fetch items on first load
    LaunchedEffect(key1 = true) {  // (key1 = true) = block runs only once
        Log.d("MarketplaceScreen", "LaunchedEffect run, gathering listings")
        viewModel.fetchNextPage(pageCategoryID)
    }
    // Show toast when adding new item
    viewModel.listener = object : MarketplaceViewModel.MarketplaceListener {
        override fun onItemAddedSuccess() {
            Toast.makeText(context,
                context.getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show()
        }
        override fun onError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            val categoryEnum = CategoryEnum.fromId(pageCategoryID)
            val categoryTitle = categoryEnum?.getTitle(context) ?: stringResource(R.string.unknown_category)
            MarketPageTop(navController, categoryTitle, viewModel, pageCategoryID, onTagClicked)
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                //Spacer(modifier = Modifier.height(100.dp))

                if (clickedTag.value == -1) {   // If no tag has been picked
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChanged = { newText -> searchText = newText },
                        onSearchClicked = { onSearchClicked() }, // Explicitly call the function inside the lambda
                        onFilterClicked = { onFilterClicked() },
                        onClearClicked = { onClearSearchClicked() },
                        expanded = showFilterMenu,
                        onSortSelected = viewModel::handleSortOptionSelected,
                        categoryID = pageCategoryID
                    )
                } else {
                    val tagEnum = TagEnum.fromId(clickedTag.value)
                    val tagTitle =
                        tagEnum?.getTitle(context) ?: stringResource(R.string.unknown_tag)
                    val tagIcon = tagEnum?.icon ?: R.drawable.search
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Image(
                            painter = painterResource(id = tagIcon),
                            contentDescription = "Custom Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 3.dp)
                        )
                        Text(text = tagTitle, style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            IconButton(
                                onClick = { // Clear tag filter and show all items
                                    clickedTag.value = -1
                                    viewModel.onTagOrSortingSelected(pageCategoryID, null)
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.background,

                                    )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                if (uiState == UiState.LOADING && items.isEmpty()) {
                    // Initial loading state when no items have been loaded yet
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize() // Fill the size of the parent
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp)) // Apply a size to the CircularProgressIndicator
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        itemsIndexed(items) { index, item ->
                            if (index == items.lastIndex && !isPaginating) {
                                viewModel.checkAndFetchNextPage(pageCategoryID)
                            }
                            ItemCard(
                                listing = item,
                                sharedViewModel = sharedViewModel,
                                navController,
                                modifier = Modifier.weight(1f),
                                index = index
                            )
                        }
                        item {
                            // Show the loading spinner at the bottom if more items are being loaded
                            if (isPaginating) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize() // Fill the size of the parent
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp)) // Apply a size to the CircularProgressIndicator
                                }
                            }
                        }
                    }
                }

                if (uiState == UiState.EMPTY) {
                    Text(stringResource(R.string.no_items_found))
                }

            }
        }
    }
}


@Composable
fun MarketPageTop(
    navController: NavController,
    category: String,
    viewModel: MarketplaceViewModel,
    categoryID: Int,
    onTagClicked: (Int) -> Unit
) {
    val topHeight = 110

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(    //orange box
            modifier = Modifier
                .height(topHeight.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        //colors = listOf(Color(0xFFFD5A0F), Color(0xFFFD7232))
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.surfaceTint
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(start = 0.dp, top = 0.dp),
                //horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = category,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.padding(end=48.dp))  // Size of IconButton. Used to center title

                }
                Spacer(modifier = Modifier.padding(top=0.dp))

                VerticalScrollTags(viewModel, categoryID, onTagClicked)

            }
        }

        Box(
            modifier = Modifier
                .padding(top = (topHeight - 25).dp)
                .height(25.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp, 25.dp))
                .background(colorResource(R.color.white))
        )
    }
}


@Composable
fun VerticalScrollTags(
    viewModel: MarketplaceViewModel,
    categoryID: Int,
    onTagClicked: (Int) -> Unit
){
    val context = LocalContext.current
    val tags = TagEnum.getAllTags()

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(tags.size) { index ->
            val tag = tags[index]
            Box(
                Modifier
                    .padding(vertical = 5.dp)
                    .clickable {
                        onTagClicked(tag.id)
                        viewModel.onTagOrSortingSelected(categoryID, index + 1)

                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = tag.icon),
                        contentDescription = "Custom Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 3.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = tag.getTitle(context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }
}