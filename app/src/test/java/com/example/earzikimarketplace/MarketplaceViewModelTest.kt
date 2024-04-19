package com.example.earzikimarketplace

import android.util.Log
import com.example.earzikimarketplace.data.model.dataClass.Listing
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.ui.viewmodel.MarketplaceViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.anyOrNull
import java.util.Date
import java.util.UUID

@ExperimentalCoroutinesApi
class MarketplaceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MarketplaceViewModel

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)

        val mockListingsDB: ListingsDB = mockk(relaxed = true)
        val expectedResult = listOf(
            Listing(
                listing_id = UUID.randomUUID(),
                title = "Mock Listing",
                description = "This is a mock description",
                category_id = 1,
                price = 99.99f,
                image_urls = listOf("http://example.com/image1.jpg"),
                active = true,
                post_date = Date(),
                ListingTags = listOf()
            )
        )

        coEvery {
            mockListingsDB.getItems(any(), any(), any(), anyOrNull(), any(), any(), any())
        } returns Result.success(expectedResult)

        viewModel = MarketplaceViewModel(mockListingsDB)
    }

    @Test
    fun testFetchNewPage() = runTest {
        viewModel.fetchNextPage(1)

        assertTrue(viewModel.items.value?.isNotEmpty() ?: false)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher to the original dispatcher
        //testDispatcher.cleanupTestCoroutines()
    }
}
