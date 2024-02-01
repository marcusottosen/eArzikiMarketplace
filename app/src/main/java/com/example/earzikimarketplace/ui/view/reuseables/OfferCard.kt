package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.earzikimarket.R
import com.example.earzikimarketplace.data.model.dataClass.Category
import com.example.earzikimarketplace.ui.viewmodel.HomeViewModel
import com.example.earzikimarketplace.data.model.dataClass.DBCategory

@Composable
fun OfferCard(category: DBCategory) {
    Card(
        shape = RoundedCornerShape(8.dp),
        //elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 0.dp),
        //.height(240.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Column (modifier = Modifier.background(colorResource(R.color.white))
        ){
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = "Local Offers from shops and restaurants",
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
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (category.itemCount.isSuccess) {
                        val count = category.itemCount.getOrNull() ?: 0
                        "$count items"
                    } else {
                        "Loading..."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
