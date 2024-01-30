package com.example.earzikimarketplace.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.earzikimarketplace.data.model.dataClasss.Listing

class SharedViewModel : ViewModel() {
    private val _listing = MutableLiveData<Listing>()
    val listing: LiveData<Listing> get() = _listing

    fun setItem(listing: Listing) {
        _listing.value = listing
    }
}

