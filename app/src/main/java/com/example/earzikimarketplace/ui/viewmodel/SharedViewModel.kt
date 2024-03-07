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
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.loadUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        _listing.value = listing
    }

    private val _userResult = MutableStateFlow<Result<UserSignUp>?>(null)
    val userResult: StateFlow<Result<UserSignUp>?> = _userResult
    fun fetchUser(userId: UUID) {
        viewModelScope.launch {
            _userResult.value = try {
                // Success case, loadUser returns a User object
                Log.d("SharedViewModel", "fetching user: $userId")
                Result.success(loadUser(userId))
            } catch (e: Exception) {
                Log.e("SharedViewModel", "failed to fetch user: $userId")
                // Error case, wrap the exception in Result
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

    fun fetchItemImages(urls: List<String>) { // Used on product page
        viewModelScope.launch {
            _imagesData.value = urls.mapNotNull { url ->
                try {
                    val bytes = listingsDB.getItemImage(url)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap.asImageBitmap()
                } catch (e: Exception) {
                    Log.e("SharedViewModel", "Failed to fetch image: $url", e)
                    null
                }
            }
        }
    }
    suspend fun fetchImageBitmap(url: String): ImageBitmap? {
        return try {
            val bytes = listingsDB.getItemImage(url)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("ItemCard", "Failed to fetch image: $url", e)
            null
        }
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

