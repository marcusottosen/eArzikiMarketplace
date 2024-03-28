package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.CategoryEnum
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.TagEnum
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddItemViewModel() : ViewModel() {
    //private val _items = MutableLiveData<List<Listing>>() //actual list of all items
    private val listingsDB = ListingsDB()
    var listener: MarketplaceViewModel.MarketplaceListener? = null   // Listener property

    private val _addItemStatus = MutableStateFlow<AddItemStatus>(AddItemStatus.Idle)
    val addItemStatus: StateFlow<AddItemStatus> = _addItemStatus


    // Stores the listing until images has been added too.
    private var temporaryListing: Listing? = null

    val allTags = mutableStateListOf(*TagEnum.values())

    // State for tracking selected tags
    val selectedTags = mutableStateListOf<TagEnum>()

    sealed class AddItemStatus {
        object Idle : AddItemStatus()
        object Loading : AddItemStatus()
        object Success : AddItemStatus()
        class Error(val message: String) : AddItemStatus()
    }

    fun toggleTagSelection(tag: TagEnum) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
        } else {
            selectedTags.add(tag)
        }
    }

    private val _selectedImageUris = MutableLiveData<List<Uri>>(emptyList())
    val selectedImageUris: LiveData<List<Uri>> get() = _selectedImageUris
    fun addSelectedImageUri(uri: Uri?) {
        uri?.let {
            val updatedUris = _selectedImageUris.value.orEmpty().toMutableList()
            if (updatedUris.size < 4) {
                updatedUris.add(uri)
                _selectedImageUris.value = updatedUris
            }
        }
    }

    // Clears all temporary info, so that new items can be added
    private fun resetAfterUpload() {
        _selectedImageUris.value = emptyList()
        temporaryListing = null
        selectedTags.clear()
    }

    // Handles if the user removes a picked image
    fun removeSelectedImageUri(index: Int) {
        val updatedUris = _selectedImageUris.value.orEmpty().toMutableList()
        if (index in updatedUris.indices) {
            updatedUris.removeAt(index)
            _selectedImageUris.value = updatedUris
        }
    }

    fun checkAndAddListing(context: Context, title: String, description: String, price: String, categoryId: CategoryEnum?, imageUrls: List<String>) {
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null) {
            val errorMessage = context.getString(R.string.please_fill_all_fields)
            listener?.onError(errorMessage)
            return
        }

        val priceFloat = price.toFloatOrNull()
        if (priceFloat == null) {
            val errorMessage = context.getString(R.string.invalid_price)
            listener?.onError(errorMessage)
            return
        }

        val listing = Listing(
            title = title,
            description = description,
            price = priceFloat,
            category_id = categoryId.id,
            image_urls = imageUrls
        )
        Log.d("AddItemViewModel", "Preparing listing: $listing")
        Log.d("AddItemViewMdoel status", _addItemStatus.value.toString())
        temporaryListing = listing
        listener?.onValidationSuccess() // Notify UI to nav to image upload screen
    }


    fun uploadImagesAndAddItem(context: Context) {
        Log.d("AddItemViewModel", "uploadImagesAndAddItem: $temporaryListing")
        viewModelScope.launch {
            _addItemStatus.value = AddItemStatus.Loading
            val imageUris = _selectedImageUris.value ?: return@launch

            try {
                val userInfo = SupabaseManager.getLoggedInUser() // retrieves user info
                val imageUrls = imageUris.map { uri ->
                    val imageByteArray = convertUriToByteArray(context, uri)
                    listingsDB.uploadImageToSupabase(imageByteArray, userInfo.id)
                }

                val updatedListing = temporaryListing?.copy(image_urls = imageUrls, user_id = UUID.fromString(userInfo.id))
                Log.d("MarketplaceViewModel", "Adding item: $updatedListing")
                val listingID = updatedListing?.let { listingsDB.addItem(it) }

                if (listingID != null) {
                    listingsDB.attachTags(listingID, selectedTags)
                }

                _addItemStatus.value = AddItemStatus.Success
                listener?.onItemAddedSuccess()
                resetAfterUpload()
            } catch (e: Exception) {
                Log.e("MarketplaceViewModel", "Error: ${e.message}")
                _addItemStatus.value = AddItemStatus.Error(e.message ?: "Unknown error")
                listener?.onError("Error: ${e.message}")
            }
        }
    }

    // Function to convert Uri to ByteArray
    // Bitmap compresses file size significantly (ex. 1.35MB -> 300KB)
    private fun convertUriToByteArray(context: Context, uri: Uri): ByteArray {
        val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

        // Resize the image for upload
        val resizedBitmap = resizeImageForUpload(originalBitmap, 720) // for 720p height

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        // Clean up
        originalBitmap.recycle()
        resizedBitmap.recycle()

        return outputStream.toByteArray()
    }

    // Resize to make image file size even smaller
    private fun resizeImageForUpload(bitmap: Bitmap, targetHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toDouble() / bitmap.height.toDouble()
        val targetWidth = (targetHeight * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}
