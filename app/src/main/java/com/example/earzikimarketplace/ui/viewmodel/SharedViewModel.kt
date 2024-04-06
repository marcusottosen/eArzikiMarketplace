package com.example.earzikimarketplace.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.speech.tts.TextToSpeech
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
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.Location
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager.getSession
import com.example.earzikimarketplace.data.model.supabaseAdapter.getLocationData
import com.example.earzikimarketplace.data.model.supabaseAdapter.loadUser
import com.example.earzikimarketplace.data.util.ImageCache
import com.example.earzikimarketplace.data.util.getCurrentLocale
import com.example.earzikimarketplace.data.util.getLanguageLocaleString
import com.example.earzikimarketplace.data.util.getLocalizedLanguageName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class SharedViewModel(
    application: Application,
    private val startActivity: (Intent) -> Unit,
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    var textToSpeech: TextToSpeech? = null

    init {
        val initialLocale = getLanguageLocaleString(context)

        textToSpeech = TextToSpeech(application.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setTextToSpeechLanguage(initialLocale)
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }
    private fun setTextToSpeechLanguage(locale: Locale) {
        Log.d("LOCALE LANGUAGE", locale.toString())
        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "Language not supported: $locale")
            textToSpeech?.setLanguage(Locale.FRENCH)    // Defaulting to french if language is not supported (Hausa is not)
        }
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    public override fun onCleared() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onCleared()
    }

    fun updateLanguage() {
        val newLocale = getLanguageLocaleString(context)
        setTextToSpeechLanguage(newLocale)
    }
    // ViewModel modification for testing
    fun updateLanguageForTest(locale: Locale) {
        setTextToSpeechLanguage(locale)
    }




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

    fun textWhatsApp(phoneNumber: String, itemTitle: String, context: Context) {
        val defaultMessage = context.getString(R.string.whatsapp_text, itemTitle)
        val encodedMessage = Uri.encode(defaultMessage)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=$encodedMessage"
        val openWhatsAppIntent = Intent(Intent.ACTION_VIEW)
        openWhatsAppIntent.data = Uri.parse(url)
        startActivity(openWhatsAppIntent)
    }

    fun sendSMS(phoneNumber: String, itemTitle: String, context: Context) {
        val defaultMessage = context.getString(R.string.sms_text, itemTitle)

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
    fun fetchItemImages(urls: List<String>, context: Context) {
        viewModelScope.launch {
            val images = urls.mapNotNull { url ->
                async {
                    ImageCache.get(url) ?: try {
                        val bytes = listingsDB.getItemImage(context, url)
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
    suspend fun fetchImageBitmap(url: String, context: Context): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            val bytes = listingsDB.getItemImage(context, url) // Runs off the main thread

            // Decode image dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true // Only decode image size, not the whole image, to avoid memory allocation
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) // Find image dimensions

            // Calculate the inSampleSize value to decode the image to a smaller version to save memory
            options.inSampleSize = calculateInSampleSize(options, 150, 150) // Calculate optimal inSampleSize value
            options.inJustDecodeBounds = false // Now settings to decode the full bitmap.

            // Decode bitmap with inSampleSize set
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("ItemCard", "Failed to fetch image: $url", e)
            null
        }
    }

    // Calculate inSampleSize for use in a BitmapFactory.
    // bitmaps to save memory consumption.
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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
            context: Context,
            application: Application,
            startActivity: (Intent) -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                    return SharedViewModel(application, startActivity) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }


}

