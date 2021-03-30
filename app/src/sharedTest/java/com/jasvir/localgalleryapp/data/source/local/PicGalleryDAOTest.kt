package com.jasvir.localgalleryapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jasvir.localgalleryapp.data.models.ImageUri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

/**
 * @author Jasvir
 */


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class PicGalleryDAOTest {

    private lateinit var database: PicGalleryDatabase
    private lateinit var picGalleryDAO: PicGalleryDAO

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), PicGalleryDatabase::class.java).build()
        picGalleryDAO = database.getPicGalleryDAO()
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
        picGalleryDAO.savePic(imageUri)
        picGalleryDAO.savePic(imageUri2)
        picGalleryDAO.savePic(imageUri3)

        // retrieve list
        val imagesTemp = picGalleryDAO.fetchUris()

        assertThat(imagesTemp[0].uri).isEqualTo(images[0].uri)

    }

    @Test
    fun deleteImages() = runBlockingTest {
        // Insert images
        picGalleryDAO.savePic(imageUri)
        picGalleryDAO.savePic(imageUri2)
        picGalleryDAO.savePic(imageUri3)

        // Delete pictures
        picGalleryDAO.deletePictures()

        // retrieve list
        val imagesTemp = picGalleryDAO.fetchUris()

        assertThat(imagesTemp.isEmpty(), `is`(true))
    }

}