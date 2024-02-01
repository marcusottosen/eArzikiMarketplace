package com.example.earzikimarketplace.ui.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.earzikimarket.R
import com.example.earzikimarketplace.data.model.dataClass.DBCategory
import com.example.earzikimarketplace.ui.view.reuseables.CategoryCard
import com.example.earzikimarketplace.ui.view.reuseables.OfferCard
import com.example.earzikimarketplace.ui.viewmodel.HomeViewModel
import com.example.earzikimarketplace.data.util.NavigationRoute

@Composable
fun Home(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val context = LocalContext.current

    viewModel.fetchCategories(context)
    val categories by viewModel.categories.observeAsState(emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ){
        item{
            HomePageTop()
        }
        item{
            CategoryList(navController, categories)
            Spacer(modifier = Modifier.height(20.dp))

        }
        item{
            OfferCard(categories[4])
            Spacer(modifier = Modifier.height(20.dp))
        }
        item{

            Button(
                onClick = { navController.navigate(NavigationRoute.AddItem.route)
                    //viewModel.addItem(testItem)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add Item")
            }
        }
    }
}

@Composable
fun HomePageTop() {
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
                        colors = listOf(Color(0xFFFD5A0F), Color(0xFFFD7232))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 15.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "eArziki",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Marketplace",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Image(
                    painter = painterResource(R.drawable.home_crafts_image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterVertically)
                        .clip(CircleShape)
                )
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
fun CategoryList(navController: NavController, categories: List<DBCategory>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 0.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            CategoryCard(
                category = categories[0],
                modifier = Modifier.weight(1f),
                navController = navController
            )
            Spacer(modifier = Modifier.width(15.dp))
            CategoryCard(
                category = categories[1],
                modifier = Modifier.weight(1f),
                navController = navController
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            CategoryCard(
                category = categories[2],
                modifier = Modifier.weight(1f),
                navController = navController
            )
            Spacer(modifier = Modifier.width(15.dp))
            CategoryCard(
                category = categories[3],
                modifier = Modifier.weight(1f),
                navController = navController
            )
        }
    }
}


