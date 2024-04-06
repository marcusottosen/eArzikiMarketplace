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

/**
 * ViewModel responsible for adding items to the marketplace.
 */
class AddItemViewModel() : ViewModel() {
    private val listingsDB = ListingsDB()   // Database handler
    var listener: MarketplaceViewModel.MarketplaceListener? = null   // Listener property for UI

    // State flow for tracking add item status
    private val _addItemStatus = MutableStateFlow<AddItemStatus>(AddItemStatus.Idle)
    val addItemStatus: StateFlow<AddItemStatus> = _addItemStatus

    // Stores the listing until images has been added too.
    private var temporaryListing: Listing? = null

    val allTags = mutableStateListOf(*TagEnum.values())

    // State for tracking selected tags
    val selectedTags = mutableStateListOf<TagEnum>()

    /**
     * Enum representing the status of adding an item.
     */
    sealed class AddItemStatus {
        object Idle : AddItemStatus()
        object Loading : AddItemStatus()
        object Success : AddItemStatus()
        class Error(val message: String) : AddItemStatus()
    }

    /**
     * Toggles the selection of a tag.
     * @param tag The tag to toggle.
     */
    fun toggleTagSelection(tag: TagEnum) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
        } else {
            selectedTags.add(tag)
        }
    }

    /**
     * Adds a selected image URI to the list of selected image URIs.
     * Limits the number of selected images to 4.
     * @param uri The URI of the selected image.
     */
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

    /**
     * Clears all temporary info, so that new items can be added
     * @param index The index of the URI to be removed.
     */
    private fun resetAfterUpload() {
        _selectedImageUris.value = emptyList()
        temporaryListing = null
        selectedTags.clear()
    }

    /**
     * Handles if the user removes a picked image
     * @param index The index of the URI to be removed.
     */
    fun removeSelectedImageUri(index: Int) {
        val updatedUris = _selectedImageUris.value.orEmpty().toMutableList()
        if (index in updatedUris.indices) {
            updatedUris.removeAt(index)
            _selectedImageUris.value = updatedUris
        }
    }

    /**
     * Checks the validity of item information and initiates the process of adding the item.
     * @param context The application context.
     * @param title The title of the item.
     * @param description The description of the item.
     * @param price The price of the item.
     * @param categoryId The ID of the category to which the item belongs.
     * @param imageUrls The URLs of the images associated with the item.
     */
    fun checkAndAddListing(
        context: Context,
        title: String,
        description: String,
        price: String,
        categoryId: CategoryEnum?,
        imageUrls: List<String>
    ) {
        // Validation checks for item information
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null) {
            val errorMessage = context.getString(R.string.please_fill_all_fields)
            listener?.onError(errorMessage)
            return
        }

        // converts price to float
        val priceFloat = price.toFloatOrNull()
        if (priceFloat == null) {
            val errorMessage = context.getString(R.string.invalid_price)
            listener?.onError(errorMessage)
            return
        }

        // Create a new listing with the provided information
        val listing = Listing(
            title = title,
            description = description,
            price = priceFloat,
            category_id = categoryId.id,
            image_urls = imageUrls
        )
        temporaryListing = listing
        listener?.onValidationSuccess() // Notify UI to nav to image upload screen
    }

    /**
     * Uploads images associated with the item to the database and adds the item to the marketplace.
     * @param context The application context.
     */
    fun uploadImagesAndAddItem(context: Context) {
        viewModelScope.launch {
            _addItemStatus.value = AddItemStatus.Loading
            val imageUris = _selectedImageUris.value ?: return@launch

            try {
                val userInfo = SupabaseManager.getLoggedInUser() // retrieves user info
                val imageUrls = imageUris.map { uri ->
                    val imageByteArray = convertUriToByteArray(context, uri)
                    listingsDB.uploadImageToSupabase(imageByteArray, userInfo.id)
                }

                val updatedListing = temporaryListing?.copy(
                    image_urls = imageUrls,
                    user_id = UUID.fromString(userInfo.id)
                )
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

    /**
     * Converts a URI to a byte array representing the image data.
     * This function first retrieves the original bitmap from the provided URI,
     * then resizes the bitmap to reduce file size, and finally compresses it
     * as a JPEG image with a compression quality of 40%.
     * Bitmap compresses file size significantly (ex. 1.35MB -> 300KB)
     *
     * @param context The application context.
     * @param uri The URI of the image.
     * @return The byte array representing the image data.
     */
    private fun convertUriToByteArray(context: Context, uri: Uri): ByteArray {
        val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

        // Resize the image for upload
        val resizedBitmap = resizeImageForUpload(originalBitmap, 720) // for 720p height

        // Compress the resized bitmap as a JPEG image with 40% quality
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        // Clean up
        originalBitmap.recycle()
        resizedBitmap.recycle()

        return outputStream.toByteArray()
    }

    /**
     * Resizes image to make the size even smaller
     *
     * @param bitmap The original bitmap to resize.
     * @param targetHeight The target height of the resized bitmap.
     * @return The resized bitmap.
     */
    private fun resizeImageForUpload(bitmap: Bitmap, targetHeight: Int): Bitmap {
        // Calculate the aspect ratio of the original bitmap
        val aspectRatio = bitmap.width.toDouble() / bitmap.height.toDouble()

        // Calculate the target width based on the aspect ratio and target height
        val targetWidth = (targetHeight * aspectRatio).toInt()

        // Scale the bitmap preserving aspect ratio
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}
