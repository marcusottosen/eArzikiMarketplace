package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.decode.DecodeUtils.calculateInSampleSize
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.Location
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager.getSession
import com.example.earzikimarketplace.data.model.supabaseAdapter.getLocationData
import com.example.earzikimarketplace.data.model.supabaseAdapter.loadUser
import com.example.earzikimarketplace.data.util.ImageCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class SharedViewModel(private val startActivity: (Intent) -> Unit) : ViewModel() {
    private val listingsDB = ListingsDB()

    private val _listing = MutableLiveData<Listing>()
    val listing: LiveData<Listing> get() = _listing

    // Updated to hold a list of ImageBitmaps for the images
    private val _imagesData = MutableStateFlow<List<ImageBitmap>?>(null)
    val imagesData: StateFlow<List<ImageBitmap>?> = _imagesData

    fun setItem(listing: Listing) {
        _imagesData.value = null    // Clear the previous images
        _userResult.value = null    // Clear the previous result
        _locationResult.value = null    // Clear the previous location
        _listing.value = listing
    }

    // Toggles for image loading. If disabled images are not loaded on marketplace page.
    private val _imageLoadingEnabled = MutableLiveData(true)
    val imageLoadingEnabled: LiveData<Boolean> = _imageLoadingEnabled
    fun toggleImageLoading() {
        _imageLoadingEnabled.value = !(_imageLoadingEnabled.value ?: true)
    }

    private val _userResult = MutableStateFlow<Result<UserSignUp>?>(null)
    val userResult: StateFlow<Result<UserSignUp>?> = _userResult

    private val _locationResult = MutableStateFlow<Result<Location>?>(null)
    val locationResult: StateFlow<Result<Location>?> = _locationResult
    fun fetchUser(userId: UUID) {
        viewModelScope.launch {
            _userResult.value = try {
                val user = loadUser(userId)
                Log.d("SharedViewModel", "fetching user: $userId")
                // If loading the user succeeds, attempt to load their location data next
                val locationId = user.location_id ?: throw Exception("User has no location ID")
                fetchLocationData(locationId)
                Result.success(user)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "failed to fetch user: $userId, error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // Checks user's session at app startup
    fun checkSession(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val session = getSession() // Check the session
            onResult(session.isNotEmpty() && session != "null")
        }
    }

    private fun fetchLocationData(locationId: UUID) {
        viewModelScope.launch {
            _locationResult.value = try {
                val location = getLocationData(locationId)
                Log.d("SharedViewModel", "fetching location: $locationId")
                Result.success(location)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "failed to fetch location: $locationId, error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    fun textWhatsApp(phoneNumber: String, itemTitle: String) {
        val defaultMessage = "Hello, I'm interested in the $itemTitle you listed on eArziki Marketplace. \nIt it still available?"
        val encodedMessage = Uri.encode(defaultMessage)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=$encodedMessage"
        val openWhatsAppIntent = Intent(Intent.ACTION_VIEW)
        openWhatsAppIntent.data = Uri.parse(url)
        startActivity(openWhatsAppIntent)
    }

    fun sendSMS(phoneNumber: String, itemTitle: String, context: Context) {
        val defaultMessage = "Hello, I'm interested in the $itemTitle you listed on eArziki Marketplace. \nIt it still available?"

        try {
            val uri = Uri.parse("sms:$phoneNumber")
            val sendIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                putExtra("sms_body", defaultMessage)
            }
            //sendIntent.putExtra("address", phoneNumber)
            //sendIntent.putExtra("sms_body", defaultMessage)
            context.startActivity(sendIntent)

        } catch (e: Exception) {
            Toast.makeText(context, "No SMS app found.", Toast.LENGTH_SHORT).show()
        }
    }

    // Used on product page. Gets all images for item
    fun fetchItemImages(urls: List<String>) {
        viewModelScope.launch {
            val images = urls.mapNotNull { url ->
                async {
                    ImageCache.get(url) ?: try {
                        val bytes = listingsDB.getItemImage(url)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()?.also { bitmap ->
                            ImageCache.put(url, bitmap)
                        }
                    } catch (e: Exception) {
                        Log.e("SharedViewModel", "Failed to fetch image: $url", e)
                        null
                    }
                }
            }.awaitAll()  // Wait for all async operations to complete

            _imagesData.value = images.filterNotNull()
        }
    }


    // Used on each card in the marketplace. Retrieves only the first image.
    suspend fun fetchImageBitmap(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            val bytes = listingsDB.getItemImage(url) // Runs off the main thread

            // Decode image dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 150, 150)
            options.inJustDecodeBounds = false

            // Decode bitmap with inSampleSize set
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("ItemCard", "Failed to fetch image: $url", e)
            null
        }
    }

    // Calculate inSampleSize for use in a BitmapFactory.
    // bitmaps to save memory consumption.
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }





    // Used for intent needed for WhatsApp API
    companion object {
        fun provideFactory(
            startActivity: (Intent) -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                    return SharedViewModel(startActivity) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}

