package com.example.earzikimarketplace.ui.view.reuseables

import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import com.example.earzikimarketplace.data.util.NavigationRoute
import kotlinx.serialization.json.Json

@Composable
fun ItemCard(listing: Listing, sharedViewModel: SharedViewModel, navController: NavController, modifier: Modifier, index: Int) {
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
            Image(
                painter = painterResource(id = R.drawable.home_crafts_image),
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
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
    Log.d("Navigation", "JSON String: $jsonString")
    navController.currentBackStackEntry?.arguments?.putString("itemJson", jsonString)
    navController.navigate(NavigationRoute.ItemDetails.route)

}





/*
fun gotoItemDetails(item: Item, navController: NavController) {
    Log.e("gotoItemDetails", item.toString())

    navController.currentBackStackEntry?.arguments?.putParcelable("item", item)
    navController.navigate(NavigationRoute.ItemDetails.route)
}*/