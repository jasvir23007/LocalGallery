package com.jasvir.localgalleryapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers.`is`
import com.jasvir.localgalleryapp.data.models.Result.Success

/**
 * @author Jasvir
 *
 * Integration test for the [PicGalleryLocalDataSource].
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class PicGalleryLocalDataSourceTest {

    private lateinit var database: PicGalleryDatabase
    private lateinit var localDataSource: PicGalleryLocalDataSource

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), PicGalleryDatabase::class.java).build()
        localDataSource = PicGalleryLocalDataSource(database.getPicGalleryDAO(), Dispatchers.Main)
    }

    @After
    fun closeDB() = database.close()

    private val imageUri = ImageUri("Uri1")
    private val imageUri2 = ImageUri("Uri2")
    private val imageUri3 = ImageUri("Uri3")
    private val images = listOf(imageUri, imageUri2, imageUri3)

    @Test
    fun insertImageAndGet() = runBlockingTest {

        // Insert images
        localDataSource.savePicture(imageUri.uri)
        localDataSource.savePicture(imageUri2.uri)
        localDataSource.savePicture(imageUri3.uri)

        // retrieve list
        val imagesTemp = localDataSource.fetchPictures(0, 30) as Success

        assertThat(imagesTemp.data[0].uri).isEqualTo(images[0].uri)

    }

    @Test
    fun deleteImages() = runBlockingTest {
        // Insert images
        localDataSource.savePicture(imageUri.uri)
        localDataSource.savePicture(imageUri2.uri)
        localDataSource.savePicture(imageUri3.uri)

        // Delete pictures
        localDataSource.deletePics()

        // retrieve list
        val imagesTemp = localDataSource.fetchPictures(0, 30) as Success

        assertThat(imagesTemp.data.isEmpty(), `is`(true))
    }

}