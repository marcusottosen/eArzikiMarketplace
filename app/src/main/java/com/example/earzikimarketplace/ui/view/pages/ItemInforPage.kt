package com.example.earzikimarketplace.ui.view.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.formatDayMonth
import com.example.earzikimarketplace.data.util.formatHourMinute
import com.example.earzikimarketplace.data.util.formatYear
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemInfoPage(sharedViewModel: SharedViewModel, navController: NavController) {
    val itemState = sharedViewModel.listing.observeAsState()
    val userResult by sharedViewModel.userResult.collectAsState()

    when {
        userResult?.isSuccess == true -> {
            Log.d("ItemInfoPage", "User data true")
            val user = userResult!!.getOrNull()
            // Display user data
            // Make sure to handle the case where user is null
        }
        userResult?.isFailure == true -> {
            Log.d("ItemInfoPage", "User data false")

            val exception = userResult!!.exceptionOrNull()
            // Handle the error
        }
        else -> {
            Log.d("ItemInfoPage", "User data else")

            // Loading state or initial state
        }

    }

    val images = listOf(
        R.drawable.home_crafts_image,
        R.drawable.food_image,
        R.drawable.local_services_image
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {

            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Back"
                )
            }
        }
        item {
            itemState.value?.let { item -> // Does only run if item is not null
                item.user_id?.let { sharedViewModel.fetchUser(it) }

                Column(modifier = Modifier.padding(26.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                    ) {

                        val pagerState = rememberPagerState()
                        val scope = rememberCoroutineScope()
                        HorizontalPager(
                            pageCount = images.size,
                            state = pagerState,
                            key = { images[it] },
                            pageSize = PageSize.Fill
                        ) { index ->
                            Image(
                                painter = painterResource(id = images[index]),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(images.size) { iteration ->
                                val color =
                                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.White
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(9.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                //.offset(y = -(16).dp)
                                .fillMaxWidth()
                                //.background(MaterialTheme.colorScheme.tertiary)
                                .align(Alignment.Center)
                        ) {

                            if (pagerState.currentPage > 0) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pagerState.currentPage - 1
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .size(60.dp)
                                        .offset(x = -(16).dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Go back",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            if (pagerState.currentPage < images.size - 1) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pagerState.currentPage + 1
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(60.dp)
                                        .offset(x = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Go forward",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text( // Price
                        text = "${item.price} CFA.",
                        //style = TextStyle(fontSize = 18.sp),
                        //fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        // Title
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        // Category/tags
                        text = "category ID: ${item.category_id}",
                        style = MaterialTheme.typography.labelMedium,
                    )


                    Text( // Posted date
                        text = "Posted: ${item.post_date?.let { formatDayMonth(it) }}/${
                            item.post_date?.let {
                                formatYear(
                                    it
                                )
                            }
                        } - ${item.post_date?.let { formatHourMinute(it) }}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 26.dp)
                    )
                    Text(
                        // location
                        text = "location..",
                        style = MaterialTheme.typography.labelMedium,
                    )


                    Text( // Description
                        text = "Description: ${item.description}",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 16.dp)
                    )


                    if (userResult != null) {
                        userResult?.let {
                            if (it.isSuccess) {
                                userContactInfo(user = userResult.getOrNull()!!)
                            }
                        }
                    }
                    userContactInfo(user = userResult?.getOrNull()!!)


                    Text( // name
                        text = "name: ${userResult?.getOrNull()?.firstname ?: "Loading..." } ${userResult?.getOrNull()?.firstname}",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text( // phone number
                        text = "Phone: ${userResult?.getOrNull()?.phone_number ?: "Loading..." }",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 16.dp)
                    )



                }
            }
        }
    }
}

@Composable
fun userContactInfo(user: UserSignUp){

    Text( // phone number
        text = "Phone: ${user.phone_number}",
        style = TextStyle(fontSize = 18.sp),
        modifier = Modifier.padding(top = 16.dp)
    )
}