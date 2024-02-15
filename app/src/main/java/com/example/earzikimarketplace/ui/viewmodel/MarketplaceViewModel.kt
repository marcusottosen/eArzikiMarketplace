package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.TagItem
import com.example.earzikimarketplace.data.model.dataClass.UiState
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class MarketplaceViewModel() : ViewModel() {

    private val _items = MutableLiveData<List<Listing>>() //actual list of all items
    private val listingsDB = ListingsDB()

    interface MarketplaceListener { // Used to display toast in the UI
        fun onItemAddedSuccess()
        fun onError(message: String)
    }
    var listener: MarketplaceListener? = null   // Listener property

    private val pageSize:Long= 10
    private val _currentPage = MutableLiveData(0)
    private val _isLoading = MutableLiveData(false)

    val items: LiveData<List<Listing>> get() = _items
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _allItemsLoaded = MutableLiveData(false)
    //val allItemsLoaded: LiveData<Boolean> = _allItemsLoaded


    // Logic to handle UI states
    private val _uiState = MediatorLiveData<UiState>()

    init {
        // Add sources to MediatorLiveData
        _uiState.addSource(_items) { updateUiState() }
        _uiState.addSource(_isLoading) { updateUiState() }

        // Initialize with loading state
        _uiState.value = UiState.LOADING
    }
    val uiState: LiveData<UiState> = _uiState

    fun checkAndFetchNextPage(categoryId: Int) {
        if ((items.value?.size
                ?: 0) >= _currentPage.value!! * pageSize && !_isLoading.value!! && !_allItemsLoaded.value!!
        ) {
            fetchNextPage(categoryId)
        }
    }


    // Update the UI state based on the data
    private fun updateUiState() {
        _uiState.value = when {
            _isLoading.value == true -> UiState.LOADING
            _items.value.isNullOrEmpty() -> UiState.EMPTY
            else -> UiState.CONTENT
        }
    }

    // Call this method when the tag changes
    fun onTagSelected(categoryId: Int, tag: Int) {
        clearItems()
        fetchNextPage(categoryId, tag)
    }

    private val _isPaginating = MutableLiveData(false)
    val isPaginating: LiveData<Boolean> get() = _isPaginating

    fun fetchNextPage(category: Int, tag: Int? = null) {
        Log.d("MarketplaceViewModel", "fetchNextPage: $category / $tag")
        if (_isLoading.value == true || _allItemsLoaded.value == true || _isPaginating.value == true) return

        _isPaginating.value = true
        val currentPage = _currentPage.value ?: 0
        val start = currentPage * pageSize


        viewModelScope.launch {
            try {
                val result = if (tag == null) { // If no tag picked, get all items in category
                    listingsDB.getItems(
                        start,
                        pageSize,
                        category
                    )
                } else {
                    listingsDB.getItemsByTagId( //If tag picked, get items with that tag in category
                        start,
                        pageSize,
                        category,
                        tag,
                    )
                }
                val newItems = result.getOrNull() ?: emptyList() // Extract list from Result

                // Check for duplicates and empty responses from the server
                val existingIds = _items.value?.map { it.listing_id } ?: emptyList()
                if (newItems.any { newItem: Listing -> existingIds.contains(newItem.listing_id) } || newItems.isEmpty()) {
                    Log.d("MarketplaceViewModel", "Duplicate item found or empty list, stopping")
                    _allItemsLoaded.value = true
                } else {
                    Log.d("MarketplaceViewModel", "Adding ${newItems.size} items")
                    _items.value = (_items.value ?: emptyList()) + newItems
                    _currentPage.value = currentPage + 1
                }
                _uiState.value = if (_items.value.isNullOrEmpty()) UiState.EMPTY else UiState.CONTENT

            } catch (e: Exception) {
                // Handle error
                Log.e("MarketplaceViewModel", "Error fetching items: ${e.message}")
            } finally {
                _isPaginating.value = false // the state for fetching new data when user reaches the end of list.
                _isLoading.value = false // Controls the initial data fetch state.
            }
        }
    }

    /**
     * Clears the list of items and resets the page counter.
     * Used before searching or filtering for tags.
     */
    fun clearItems() {
        _items.value = emptyList()
        _allItemsLoaded.value = false
        _currentPage.value = 0
    }

    fun searchItems(query: String) {
        clearItems()
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = listingsDB.searchListingsByTitle(searchQuery = query)
                val newItems = result.getOrNull() ?: emptyList()
                _items.value = newItems
                _uiState.value = if (_items.value.isNullOrEmpty()) UiState.EMPTY else UiState.CONTENT
            } catch (e: Exception) {
                // Handle error
                Log.e("MarketplaceViewModel", "Error searching items: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }




/*
    private val _addItemStatus = MutableStateFlow<AddItemStatus>(AddItemStatus.Idle)
    val addItemStatus: StateFlow<AddItemStatus> = _addItemStatus
    fun resetAddItemStatus() {
        _addItemStatus.value = AddItemStatus.Idle
    }

    fun updateStatus(newStatus: AddItemStatus) {
        _addItemStatus.value = newStatus
    }*/

   /* fun addItem(context: Context, listing: Listing) {
        viewModelScope.launch {
            _addItemStatus.value = AddItemStatus.Loading
            try {
                Log.d("MarketplaceViewModel", "Adding item: $listing")
                //val userInfo = getUserInfo(apiKey, apiUrl) // retrieves user info from the database
                val userInfo = SupabaseManager.getLoggedInUser() // retrieves user info from the database

                val updatedListing = Listing(
                    user_id = UUID.fromString(userInfo.id),
                    title = listing.title,
                    description = listing.description,
                    price = listing.price,
                    category_id = listing.category_id,
                    image_urls = listing.image_urls
                )

                listingsDB.addItem(updatedListing)   // Adds item to database
                val updatedItems = (_items.value.orEmpty() + updatedListing).toMutableList()
                _items.value = updatedItems

                _addItemStatus.value = AddItemStatus.Success // Set to success after adding


                _addItemStatus.value = AddItemStatus.Success    //allows for navigation
                listener?.onItemAddedSuccess()  // Notify success by toast

            } catch (e: Exception) {
                Log.e("MarketplaceViewModel", "Error adding item: ${e.message}")
                _addItemStatus.value = AddItemStatus.Error(e.message ?: "Unknown error")

                _addItemStatus.value = e.message?.let { AddItemStatus.Error(it) }!!

                listener?.onError("Error adding item: ${e.message}")  // Notify the listener about the error
            }
        }
    }*/
}
// TODO: two listeners for the same thing? (_addItemStatus.value and listener?.onItemAddedSuccess())

