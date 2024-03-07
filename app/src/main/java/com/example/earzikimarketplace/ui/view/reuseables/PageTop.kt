package com.example.earzikimarketplace.ui.view.reuseables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earzikimarket.R

@Composable
fun PageTop(navController: NavController, title: String) {
    val topHeight = 80

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
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.padding(end=48.dp))  // Size of IconButton. Used to center title

                }
                Spacer(modifier = Modifier.padding(top=10.dp))


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