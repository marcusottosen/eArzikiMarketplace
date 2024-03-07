package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AddItemViewModel() : ViewModel() {
    private val _items = MutableLiveData<List<Listing>>() //actual list of all items
    private val listingsDB = ListingsDB()
    var listener: MarketplaceViewModel.MarketplaceListener? = null   // Listener property

    private val _addItemStatus = MutableStateFlow<AddItemStatus>(AddItemStatus.Idle)
    val addItemStatus: StateFlow<AddItemStatus> = _addItemStatus
    fun resetAddItemStatus() {
        _addItemStatus.value = AddItemStatus.Idle
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

    fun resetAfterUpload() {
        // Clear the selected image URIs
        _selectedImageUris.value = emptyList()
        //_addItemStatus.value = AddItemStatus.Idle
        temporaryListing = null
    }


    // Handles if the user removes a picked image
    fun removeSelectedImageUri(index: Int) {
        val updatedUris = _selectedImageUris.value.orEmpty().toMutableList()
        if (index in updatedUris.indices) {
            updatedUris.removeAt(index)
            _selectedImageUris.value = updatedUris
        }
    }



    // Stores the listing until images has been added too.
    var temporaryListing: Listing? = null
    fun prepareListing(listing: Listing) {
        Log.d("AddItemViewModel", "Preparing listing: $listing")
        temporaryListing = listing
    }

    sealed class AddItemStatus {
        object Idle : AddItemStatus()
        object Loading : AddItemStatus()
        object Success : AddItemStatus()
        class Error(val message: String) : AddItemStatus()
    }

    fun uploadImagesAndAddItem(context: Context) {
        Log.d("AddItemViewModel", "uploadImagesAndAddItem: $temporaryListing")
        viewModelScope.launch {
            _addItemStatus.value = AddItemStatus.Loading
            val imageUris = _selectedImageUris.value ?: return@launch

            try {
                val userInfo = SupabaseManager.getLoggedInUser() // retrieves user info from the database
                val imageUrls = imageUris.map { uri ->
                    val imageByteArray = convertUriToByteArray(context, uri)
                    listingsDB.uploadImageToSupabase(imageByteArray, userInfo.id)
                }

                val updatedListing = temporaryListing?.copy(image_urls = imageUrls)
                Log.d("MarketplaceViewModel", "Adding item: $updatedListing")
                updatedListing?.let { listingsDB.addItem(it) }

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
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

        // Compress the bitmap
        val outputStream = ByteArrayOutputStream()
        // You can adjust the quality parameter (0-100) as needed
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)

        return outputStream.toByteArray()
    }
}
