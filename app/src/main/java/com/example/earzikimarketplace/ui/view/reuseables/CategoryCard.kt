package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.DBCategory
import com.example.earzikimarketplace.data.util.NavigationRoute

@Composable
fun CategoryCard(category: DBCategory, modifier: Modifier = Modifier, navController: NavController) {

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            //.padding(end = 10.dp)
            .width(175.dp)
            .clickable {
                val route = NavigationRoute.Marketplace.createRoute(category.categoryID)
                navController.navigate(route)
            }

        //.height(240.dp)
    ) {
        Column (modifier = Modifier.background(colorResource(R.color.white))
        ){
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.categoryName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Column(
                //verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp)
                    .padding(top = 0.dp)
            ) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Visible,
                    maxLines = 2, // Allow up to two lines
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (category.itemCount.isSuccess) {
                        val count = category.itemCount.getOrNull() ?: 0
                        stringResource(R.string.items, count)
                    } else {
                        stringResource(R.string.loading)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PlaceholderCategoryCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            //.padding(end = 10.dp)
            .width(175.dp)

        //.height(240.dp)
    ) {
        Column (modifier = Modifier.background(colorResource(R.color.white))
        ){
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
            Column(
                //verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
