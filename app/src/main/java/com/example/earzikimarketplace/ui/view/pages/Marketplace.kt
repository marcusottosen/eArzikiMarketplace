package com.example.earzikimarketplace.ui.view.pages

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.CategoryEnum
import com.example.earzikimarketplace.data.model.dataClass.TagEnum
import com.example.earzikimarketplace.data.model.dataClass.UiState
import com.example.earzikimarketplace.ui.view.reuseables.ItemCard
import com.example.earzikimarketplace.ui.view.reuseables.SearchBar
import com.example.earzikimarketplace.ui.viewmodel.MarketplaceViewModel
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(sharedViewModel: SharedViewModel, navController: NavController, categoryID: Int) {
    val context = LocalContext.current
    val viewModel: MarketplaceViewModel = viewModel()

    // States and items
    val items by viewModel.items.observeAsState(initial = emptyList())
    val uiState by viewModel.uiState.observeAsState(UiState.LOADING)
    val isPaginating by viewModel.isPaginating.observeAsState(false)

    // Search and filter
    var searchText by remember { mutableStateOf("") }
    val onSearchClicked = {
        Log.d("Marketplace", "Search button clicked: $searchText")
        viewModel.searchItems(searchText)
    }
    val onFilterClicked = { Log.d("Marketplace", "Filter button clicked") }
    val onClearClicked = {
        Log.d("Marketplace", "Clear button clicked")
    searchText = ""
    }

    // Fetch items on first load
    LaunchedEffect(key1 = true) {  // (key1 = true) = block runs only once
        Log.d("MarketplaceScreen", "LaunchedEffect run, gathering listings")
        viewModel.fetchNextPage(categoryID)
    }
    // Show toast when adding new item
    viewModel.listener = object : MarketplaceViewModel.MarketplaceListener {
        override fun onItemAddedSuccess() {
            Toast.makeText(context, "Item added successfully!", Toast.LENGTH_SHORT).show()
        }
        override fun onError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            val categoryEnum = CategoryEnum.fromId(categoryID)
            val categoryTitle = categoryEnum?.title ?: "Unknown Category"
            MarketPageTop(navController, categoryTitle, viewModel, categoryID)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            SearchBar(
                searchText = searchText,
                onSearchTextChanged = { newText -> searchText = newText },
                onSearchClicked = { onSearchClicked() }, // Explicitly call the function inside the lambda
                onFilterClicked = { onFilterClicked() }, // Explicitly call the function inside the lambda
                onClearClicked = { onClearClicked() } // Explicitly call the function inside the lambda
            )
            Spacer(modifier = Modifier.height(15.dp))

            when (uiState) {
                UiState.LOADING -> Text("Loading...")
                UiState.EMPTY -> Text("No items found.")
                UiState.CONTENT -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // or adapt to your design needs
                    //contentPadding = PaddingValues(all = 8.dp),
                    ) {
                    itemsIndexed(items) { index, item ->
                        if (index == items.lastIndex && !isPaginating) {
                            viewModel.checkAndFetchNextPage(categoryID)
                        }
                        ItemCard(
                            listing = item,
                            sharedViewModel = sharedViewModel,
                            navController,
                            modifier = Modifier.weight(1f),
                            index = index
                        )
                    }
                    if (isPaginating) {
                        item {
                            CircularProgressIndicator() // Or any other loading indicator
                        }
                    }
                }

                null -> Text("Initializing...") // or another appropriate UI representation for this state
            }
            if (uiState == UiState.LOADING) {
                CircularProgressIndicator() // Or any other initial loading indicator
            }

        }
    }
}


@Composable
fun MarketPageTop(navController: NavController, category: String, viewModel: MarketplaceViewModel, categoryID: Int) {
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
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceTint)
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

                VerticalScrollCategories(viewModel, categoryID)

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
fun VerticalScrollCategories(viewModel: MarketplaceViewModel, categoryID: Int){
    //val tags1 = viewModel.generateTagItems()
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
                        viewModel.onTagSelected(categoryID, index + 1)

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
                        text = tag.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }
}
