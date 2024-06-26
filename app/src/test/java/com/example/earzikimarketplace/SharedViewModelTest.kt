package com.example.earzikimarketplace

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.test.core.app.ApplicationProvider
import com.example.earzikimarketplace.data.util.getCurrentLocale
import com.example.earzikimarketplace.data.util.getLanguageLocaleString
import com.example.earzikimarketplace.data.util.getLocalizedLanguageName
import com.example.earzikimarketplace.data.util.setLocale
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Locale
import kotlin.test.assertEquals
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R]) // R is API 30
class SharedViewModelTest {

    private lateinit var sharedViewModel: SharedViewModel

    @Mock
    private lateinit var mockTextToSpeech: TextToSpeech

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)  // Initialize mock objects
        val application = RuntimeEnvironment.application    // Create a test application context
        sharedViewModel = SharedViewModel(application) { }  // Initialize SharedViewModel
        // Assign the mockTextToSpeech to the sharedViewModel
        sharedViewModel.textToSpeech = mockTextToSpeech
        //Configuring behavior of mockTextToSpeech
        `when`(mockTextToSpeech.speak(anyString(), anyInt(), anyOrNull())).thenReturn(TextToSpeech.SUCCESS)
    }

    @Test
    fun speak_callsTextToSpeechSpeak() {
        val sampleText = "Hello, World!"     // Sample text to speak
        sharedViewModel.speak(sampleText)   // Calling speak method
        // Verifying speak method call
        verify(mockTextToSpeech).speak(eq(sampleText), eq(TextToSpeech.QUEUE_FLUSH), isNull(), isNull())
    }

    @Test
    fun updateLanguage_updatesLocaleToFrench() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()     // get context
        setLocale(context, "fr") // Set the application locale to French.
        sharedViewModel.updateLanguage()

        // Act - get current language
        val currentLanguage = getLocalizedLanguageName(getCurrentLocale(context))

        // Assert - check if its french
        assertEquals("français", currentLanguage)
    }

    @Test
    fun `getLanguageLocaleString returns correct Locale object`() {
        val context = mock(Context::class.java)

        // Stubbing the method to return a simple language code
        `when`(getCurrentLocale(context)).thenReturn("[es]")
        var expectedLocale = Locale("es")
        var resultLocale = getLanguageLocaleString(context)
        assertEquals(expectedLocale, resultLocale, "Locale should match the simple language code.")

        // Stubbing the method to return a language code with country
        `when`(getCurrentLocale(context)).thenReturn("[en-US]")
        expectedLocale = Locale("en", "US")
        resultLocale = getLanguageLocaleString(context)
        assertEquals(expectedLocale, resultLocale, "Locale should include both language and country code.")
    }
    @After
    fun tearDown() {    // Cleaning up after tests
        sharedViewModel.onCleared()
    }
}