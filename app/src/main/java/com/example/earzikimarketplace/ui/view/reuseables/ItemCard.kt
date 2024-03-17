package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.util.ImageCache
import com.example.earzikimarketplace.data.util.NavigationRoute
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun ItemCard(listing: Listing, sharedViewModel: SharedViewModel, navController: NavController, modifier: Modifier, index: Int) {
    val context = LocalContext.current

    // Local state for storing the image bitmap
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    // Observe image loading toggle
    val isImageLoadingEnabled by sharedViewModel.imageLoadingEnabled.observeAsState(true)
    val firstImageUrl = listing.image_urls?.getOrNull(0)


    // Trigger image loading based on the firstImageUrl and the image loading preference
    LaunchedEffect(firstImageUrl, isImageLoadingEnabled) {
        if (isImageLoadingEnabled && firstImageUrl != null) {
            // Check the cache first
            val cachedImage = ImageCache.get(firstImageUrl)
            if (cachedImage != null) {
                imageBitmap = cachedImage
            } else {
                // If not in the cache, load the image
                imageBitmap = sharedViewModel.fetchImageBitmap(firstImageUrl, context)?.also { bitmap ->
                    // Store the loaded image in the cache
                    ImageCache.put(firstImageUrl, bitmap)
                }
            }
        } else {
            imageBitmap = null // Optionally, set a default placeholder here
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            .padding(
                end = if (index % 2 == 0 ) 10.dp else 0.dp,
                bottom = 10.dp
            )
            .width(175.dp)
            .clickable {
                sharedViewModel.setItem(listing)
                gotoItemDetails(listing, navController)
            }

        //.height(240.dp)
    ) {
        Column (modifier = Modifier.background(colorResource(R.color.white))
        ){
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )
            } else {
                // Display a placeholder
                Image(
                    painter = painterResource(id = R.drawable.placeholder),
                    contentDescription = "Placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp)
                    .padding(top = 0.dp)
            ) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Visible,
                    maxLines = 2, // Allow up to two lines
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                    //.wrapContentHeight(align = Alignment.CenterVertically)
                )
                Text(
                    text = "CFA. ${listing.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/*
@Composable
fun ItemCard(item: Item, sharedViewModel: SharedViewModel, navController: NavController) {
    //Log.e("ItemCard", item.toString())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                sharedViewModel.setItem(item)
                gotoItemDetails(item, navController)
                       },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(R.drawable.anne),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                color = Color.Gray
            )
           /* Button(onClick = { gotoItemDetails(item, navController) }) {
                Text(text = "Navigate")

            }*/
        }
    }
}*/

fun gotoItemDetails(listing: Listing, navController: NavController) {
    val jsonString = Json.encodeToString(Listing.serializer(), listing)
    //Log.d("Navigation", "JSON String: $jsonString")
    navController.currentBackStackEntry?.arguments?.putString("itemJson", jsonString)
    navController.navigate(NavigationRoute.ItemDetails.route)

}


/*
fun gotoItemDetails(item: Item, navController: NavController) {
    Log.e("gotoItemDetails", item.toString())

    navController.currentBackStackEntry?.arguments?.putParcelable("item", item)
    navController.navigate(NavigationRoute.ItemDetails.route)
}*/