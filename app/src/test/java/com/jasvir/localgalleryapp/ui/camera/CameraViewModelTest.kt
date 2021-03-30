package com.jasvir.localgalleryapp.ui.camera

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jasvir.localgalleryapp.data.source.FakeRepository
import com.jasvir.localgalleryapp.di.AppModule
import com.jasvir.localgalleryapp.di.DataModule
import com.jasvir.localgalleryapp.util.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


/**
 * @author Jasvir
 */

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class CameraViewModelTest : KoinTest {

    // What is testing
    private lateinit var cameraViewModel: CameraViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeRepository

    val dispatchers: CoroutineDispatcher by inject()

    // Rule for koin injection
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(listOf(AppModule, DataModule))
    }

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setupViewModel() {
        repository = FakeRepository()

        cameraViewModel = CameraViewModel(mockContext, repository)
    }

    @Test
    fun saveImageFromCamera() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set photo observable
        cameraViewModel.photoFile.set(
            File.createTempFile(
                "/data/user/0/com.example.picgalleryapp/cache/image_manager_disk_cache/bd74a2e2b3f9c627dedc0d88a0dad5c9936406802a73c63516ca35492a6612c3",
                ".0"
            )
        )

        // Trigger saving of image
        cameraViewModel.saveRetake(true)

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Photo is set null after saving
        assertThat(cameraViewModel.photoFile.get()).isNull()
    }


    @Test
    fun saveImageRetake() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()


        // Set photo observable
        cameraViewModel.photoFile.set(
            File.createTempFile(
                "/data/user/0/com.example.picgalleryapp/cache/image_manager_disk_cache/bd74a2e2b3f9c627dedc0d88a0dad5c9936406802a73c63516ca35492a6612c3",
                ".0"
            )
        )

        // Trigger saving of image
        cameraViewModel.saveRetake(false)

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Photo is set null after saving
        assertThat(cameraViewModel.photoFile.get()).isNull()

    }
}