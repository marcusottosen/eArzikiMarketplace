package com.example.earzikimarketplace.ui.view.pages

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earzikimarketplace.data.util.formatDayMonth
import com.example.earzikimarketplace.data.util.formatHourMinute
import com.example.earzikimarketplace.data.util.formatYear
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.ui.view.reuseables.FullScreenImageDialog
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemInfoPage(sharedViewModel: SharedViewModel, navController: NavController) {
    val userResult by sharedViewModel.userResult.collectAsState()
    val locationResult by sharedViewModel.locationResult.collectAsState()

    val itemState = sharedViewModel.listing.observeAsState()
    itemState.value?.user_id?.let { userId ->
        LaunchedEffect(userId) {
            sharedViewModel.fetchUser(userId)
        }
    }

    val imagesData by sharedViewModel.imagesData.collectAsState()
    itemState.value?.let { item ->
        LaunchedEffect(item.image_urls) {
            item.image_urls?.let { sharedViewModel.fetchItemImages(it) }
        }
    }

    // State to show the selected image in full
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }


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

    // Display the full-screen dialog for ImageBitmap
    if (selectedImageBitmap != null) {
        FullScreenImageDialog(imageBitmap = selectedImageBitmap) {
            selectedImageBitmap = null // Clear the selected image
        }
    }

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
                Column(modifier = Modifier.padding(horizontal = 26.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                    ) {

                        val pagerState = rememberPagerState()
                        val scope = rememberCoroutineScope()

                        // Determine if we should show placeholder or actual images
                        val showPlaceholder = imagesData == null || imagesData!!.isEmpty()
                        val pageCount = if (showPlaceholder) item.image_urls?.size else imagesData!!.size

                        if (pageCount != null) {
                            HorizontalPager(
                                pageCount = pageCount,
                                state = pagerState,
                                //key = { images[it] },
                                pageSize = PageSize.Fill
                            ) { index ->
                                if (showPlaceholder) {
                                    // Display the placeholder
                                    Image(
                                        painter = painterResource(id = R.drawable.placeholder),
                                        contentDescription = "Placeholder",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    // Display the actual image
                                    imagesData?.get(index)?.let { imageBitmap ->
                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    // Assuming you have a way to convert imageBitmap or its identifier to a Uri
                                                    // This is a placeholder, you'll need to replace it with your actual logic
                                                    val uri = Uri.parse(imageBitmap.toString())
                                                    selectedImageBitmap = imageBitmap
                                                }
                                        )
                                    }
                                }
                            }
                        }
                        if (true) {
                            // Page indicator
                            Row(
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (pageCount != null) {
                                    repeat(pageCount) { iteration ->
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
                                if (pageCount != null) {
                                    if (pagerState.currentPage < pageCount - 1) {
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
                    //Text(
                    //    // Category/tags
                    //    text = "category ID: ${item.category_id}",
                    //    style = MaterialTheme.typography.labelMedium,
                    //)


                    Text( // Posted date
                        text = "${stringResource(id = R.string.posted)} ${item.post_date?.let { formatDayMonth(it) }}/${
                            item.post_date?.let {
                                formatYear(
                                    it
                                )
                            }
                        } - ${item.post_date?.let { formatHourMinute(it) }}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 26.dp)
                    )
                    Text( // location
                        text = if (locationResult?.isSuccess == true)
                            locationResult?.getOrNull()?.city.toString() + ", " + locationResult?.getOrNull()?.address.toString()
                        else
                            "",
                        style = MaterialTheme.typography.labelMedium,
                    )


                    Text( // Description
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
        item {
            itemState.value?.let { item ->
                // This will now always be visible, showing either the user data or "loading..."
                UserContactInfo(user = userResult?.getOrNull(), sharedViewModel, item.title)
            }

        }
    }
}

@Composable
fun UserContactInfo(user: UserSignUp?, viewModel: SharedViewModel, itemTitle: String){
    val context = LocalContext.current

    Column (modifier = Modifier.padding(26.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
        ) {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)) {
                Image(
                    painter = painterResource(R.drawable.home_crafts_image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column (modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)) {
                    if (user != null) {
                        Text(
                            text = user.firstname + " " + user.surname,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = "+227 ${user.phone_number.toString()}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.textWhatsApp("4560906128", itemTitle) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp), // Sets the corner shape of the button
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25d366))
            ) {
                Text(stringResource(R.string.whatsapp), color = Color.White, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.whatsapp_white_icon),
                    contentDescription = stringResource(R.string.text_seller),

                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Button(
                onClick = { viewModel.sendSMS("4560906128", itemTitle, context) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp), // Sets the corner shape of the button
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(stringResource(R.string.sms), color = Color.White, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.sms_icon),
                    contentDescription = stringResource(R.string.sms),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }


}