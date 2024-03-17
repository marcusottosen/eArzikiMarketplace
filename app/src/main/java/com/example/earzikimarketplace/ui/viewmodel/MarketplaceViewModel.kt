package com.example.earzikimarketplace.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.SortOption
import com.example.earzikimarketplace.data.model.dataClass.UiState
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import kotlinx.coroutines.launch


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

    // Sorting criteria
    private var sortByDateDescending = true
    private var sortByPrice = false
    private var priceAscending = true

    private val _isPaginating = MutableLiveData(false)
    val isPaginating: LiveData<Boolean> get() = _isPaginating

    val items: LiveData<List<Listing>> get() = _items
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




    // Update the UI state based on the data
    private fun updateUiState() {
        _uiState.value = when {
            _isLoading.value == true -> UiState.LOADING
            _items.value.isNullOrEmpty() -> UiState.EMPTY
            else -> UiState.CONTENT
        }
    }

    fun checkAndFetchNextPage(pageCategoryId: Int) {
        Log.d("MarketplaceViewmodel", "checkAndFetchNextPage")

        if ((items.value?.size
                ?: 0) >= _currentPage.value!! * pageSize && !_isLoading.value!! && !_allItemsLoaded.value!!
        ) {
            fetchNextPage(pageCategoryId)
        }
    }

    // Call this method when tag or sorting changes
    fun onTagOrSortingSelected(categoryId: Int, tag: Int?) {
        Log.d("MarketplaceViewmodel", "onTagOrSortingSelected")
        _uiState.value = UiState.LOADING
        clearItems()
        fetchNextPage(categoryId, tag)
    }



    fun fetchNextPage(category: Int, tag: Int? = null) {
        //Log.d("MarketplaceViewModel", "fetchNextPage: $category / $tag")
        if (_isLoading.value == true || _allItemsLoaded.value == true || _isPaginating.value == true) return

        _isLoading.value = true
        updateUiState()

        _isPaginating.value = true
        val currentPage = _currentPage.value ?: 0
        val start = currentPage * pageSize


        viewModelScope.launch {
            try {
                val result =
                    listingsDB.getItems(
                        start,
                        pageSize,
                        category,
                        tag,
                        sortByDateDescending = sortByDateDescending,
                        sortByPrice = sortByPrice,
                        priceAscending = priceAscending

                    )
                /*val result = if (tag == null) { // If no tag picked, get all items in category
                    listingsDB.getItems(
                        start,
                        pageSize,
                        category,
                        tag
                    )
                } else {
                    listingsDB.getItemsByTagId( //If tag picked, get items with that tag in category
                        start,
                        pageSize,
                        category,
                        tag,
                    )
                }*/
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
                //updateUiState()


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

    fun handleSortOptionSelected(optionId: Int, categoryId: Int) {
        val option = SortOption.values().find { it.id == optionId }
        when (option) {
            SortOption.NearestItems -> sortItemsByNearest()
            SortOption.DateNewest -> sortItemsByDate(sortNewestFirst = true, categoryId)
            SortOption.DateOldest -> sortItemsByDate(sortNewestFirst = false, categoryId)
            SortOption.PriceCheapest -> sortItemsByPrice(isCheapestFirst = true, categoryId)
            SortOption.PriceMostExpensive -> sortItemsByPrice(isCheapestFirst = false, categoryId)
            null -> Log.d("MarketplaceViewModel", "Unknown sort option")
        }
    }

    private fun sortItemsByNearest() {
        Log.d("marketviewmodel", "sort by nearest item")
        // Implementation of sorting items by nearest
    }

    private fun sortItemsByDate(sortNewestFirst: Boolean, categoryId: Int) {
        Log.d("marketviewmodel", "sort item by date. Newest: $sortNewestFirst")
        sortByPrice = false
        sortByDateDescending = sortNewestFirst
        onTagOrSortingSelected(categoryId = categoryId, tag = null)
    }

    private fun sortItemsByPrice(isCheapestFirst: Boolean, categoryId: Int) {
        sortByPrice = true
        priceAscending = isCheapestFirst
        onTagOrSortingSelected(categoryId = categoryId, tag = null)
    }
}

