package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.data.model.dataClass.DBCategory
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing data related to the home screen.
 */
class HomeViewModel : ViewModel() {
    // LiveData for storing the list of categories
    private val _categories = MutableLiveData<List<DBCategory>>()
    val categories: LiveData<List<DBCategory>> = _categories

    // Listings database instance for fetching item counts
    private val listingsDB = ListingsDB()

    // LiveData to track the loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _isLoading.value = true
    }

    /**
     * Fetches the categories asynchronously and updates the LiveData.
     *
     * @param context The application context.
     */
    fun fetchCategories(context: Context) {
        viewModelScope.launch {
            // Create initial categories
            val initialCategories = DBCategory.createDefaultCategories(context)
            _categories.value = initialCategories
            _isLoading.value = false // loading of initial data done

            // fetch item counts in parallel and update categories when retrieved
            val updatedCategories = initialCategories.map { category ->
                async {
                    category.copy(itemCount = fetchItemCountForCategory(category.categoryID))
                }
            }.awaitAll()

            // Update categories with the fetched item counts
            _categories.value = updatedCategories
        }
    }

    /**
     * Fetches the item count for a specific category.
     *
     * @param category The category ID.
     * @return A Result object containing the item count.
     */
    private suspend fun fetchItemCountForCategory(category: Int): Result<Int> {
        return listingsDB.getCategoryCount(category)
    }
}