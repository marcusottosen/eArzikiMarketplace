package com.example.earzikimarketplace.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.dataClass.SortOption
import com.example.earzikimarketplace.data.model.dataClass.UiState
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the data related to the marketplace.
 */
class MarketplaceViewModel(private val listingsDB: ListingsDB) : ViewModel() {

    private val _items = MutableLiveData<List<Listing>>() //actual list of all items
    private val pageSize: Long = 10
    private val _currentPage = MutableLiveData(0)
    private val _isLoading = MutableLiveData(false)
    private val _isPaginating = MutableLiveData(false)
    val isPaginating: LiveData<Boolean> get() = _isPaginating
    val items: LiveData<List<Listing>> get() = _items
    private val _allItemsLoaded = MutableLiveData(false)
    private val _uiState = MediatorLiveData<UiState>()  // Logic to handle UI states

    // Sorting criteria
    private var sortByDateDescending = true
    private var sortByPrice = false
    private var priceAscending = true


    interface MarketplaceListener { // Used for the UI to observe the ViewModel and react from it.
        fun onValidationSuccess()
        fun onItemAddedSuccess()
        fun onError(message: String)
    }

    var listener: MarketplaceListener? = null   // Listener property

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

    /**
     * Checks and fetches the next page of items if necessary.
     *
     * @param pageCategoryId The category ID of the current page.
     */
    fun checkAndFetchNextPage(pageCategoryId: Int) {
        if ((items.value?.size
                ?: 0) >= _currentPage.value!! * pageSize && !_isLoading.value!! && !_allItemsLoaded.value!!
        ) {
            fetchNextPage(pageCategoryId)
        }
    }

    /**
     * Call this method when tag or sorting changes.
     *
     * @param categoryId The category ID.
     * @param tag The tag ID.
     */
    fun onTagOrSortingSelected(categoryId: Int, tag: Int?) {
        _uiState.value = UiState.LOADING
        clearItems()
        fetchNextPage(categoryId, tag)
    }

    /**
     * Initiates the fetching of the next page of items.
     *
     * @param category The category of the items to fetch.
     * @param tag The optional tag to filter the items by.
     */
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
                val newItems = result.getOrNull() ?: emptyList() // Extract list from Result

                // Check for duplicates and empty responses from the server
                val existingIds = _items.value?.map { it.listing_id } ?: emptyList()
                if (newItems.any { newItem: Listing -> existingIds.contains(newItem.listing_id) } || newItems.isEmpty()) {
                    _allItemsLoaded.value = true
                } else {
                    _items.value = (_items.value ?: emptyList()) + newItems
                    _currentPage.value = currentPage + 1
                }
                _uiState.value =
                    if (_items.value.isNullOrEmpty()) UiState.EMPTY else UiState.CONTENT
                //updateUiState()


            } catch (e: Exception) {
                // Handle error
                Log.e("MarketplaceViewModel", "Error fetching items: ${e.message}")
            } finally {
                _isPaginating.value =
                    false // the state for fetching new data when user reaches the end of list.
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

    /**
     * Searches for items based on the given query.
     * OBS search not working
     *
     * @param query The search query.
     */
    fun searchItems(query: String) {
        clearItems()
        //Log.d("MarketplaceVM search", "Initiating search for: $query")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = listingsDB.searchListingsByTitle(searchQuery = query)
                val newItems = result.getOrNull() ?: emptyList()
                _items.value = newItems
                _uiState.value =
                    if (_items.value.isNullOrEmpty()) UiState.EMPTY else UiState.CONTENT
            } catch (e: Exception) {
                Log.e("MarketplaceViewModel", "Error searching items: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles the selection of a sort option.
     *
     * @param optionId The ID of the selected sort option.
     * @param categoryId The category ID to filter the items by.
     */
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
        // Implementation of sorting items by nearest
    }

    private fun sortItemsByDate(sortNewestFirst: Boolean, categoryId: Int) {
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

/**
 * Factory class for creating instances of [MarketplaceViewModel].
 * Created to make test function.
 *
 * @property listingsDB The [ListingsDB] instance to be used by the ViewModel.
 */
class MarketplaceViewModelFactory(private val listingsDB: ListingsDB) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketplaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketplaceViewModel(listingsDB) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
