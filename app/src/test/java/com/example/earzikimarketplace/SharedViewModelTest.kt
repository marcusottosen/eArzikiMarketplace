package com.example.earzikimarketplace

import android.app.Application
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.compose.ui.graphics.ImageBitmap
import com.example.earzikimarketplace.data.model.dataClass.UserSignUp
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.util.ImageCache
import com.example.earzikimarketplace.ui.viewmodel.SharedViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.Locale


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class UsingContextTest {

    @Test
    fun testExample() {
        // Example test
        println("This is a test executed on sdk ${Build.VERSION.SDK_INT}")
        // Assert something
    }

    init {
        println("This class is executed on sdk ${Build.VERSION.SDK_INT}")
    }
}
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R]) // R is API 30
class SharedViewModelTest {

    private lateinit var sharedViewModel: SharedViewModel

    @Mock
    private lateinit var mockTextToSpeech: TextToSpeech

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val application = RuntimeEnvironment.application
        sharedViewModel = SharedViewModel(application) { }

        // Assume TextToSpeech instance is mocked to test the interaction
        sharedViewModel.textToSpeech = mockTextToSpeech

        // If there's a need to mock a method called within `speak` that does return a value or has side effects, do it here
        // Example: Assuming TextToSpeech's speak method is called within your ViewModel's speak method
        `when`(mockTextToSpeech.speak(anyString(), anyInt(), anyOrNull())).thenReturn(TextToSpeech.SUCCESS)
    }

    @Test
    fun speak_callsTextToSpeechSpeak() {
        val sampleText = "Hello, World!"
        sharedViewModel.speak(sampleText)

        verify(mockTextToSpeech).speak(eq(sampleText), eq(TextToSpeech.QUEUE_FLUSH), isNull(), isNull())
    }

    @Test
    fun updateLanguage_setsLanguageToFrench() {
        // This test assumes that the environment or conditions now result in French being selected.
        // The actual mechanism for this isn't shown due to the constraints described.

        sharedViewModel.updateLanguage()

        val localeCaptor = ArgumentCaptor.forClass(Locale::class.java)
        verify(mockTextToSpeech).setLanguage(localeCaptor.capture())

        val capturedLocale = localeCaptor.value
        // Assert that the captured locale is French.
        assertEquals(Locale.FRENCH, capturedLocale)
    }


    @After
    fun tearDown() {
        // Here you can clean up resources, if necessary. For example:
        sharedViewModel.onCleared()
    }
}