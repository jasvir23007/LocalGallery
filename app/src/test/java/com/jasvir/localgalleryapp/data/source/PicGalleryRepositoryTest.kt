package com.jasvir.localgalleryapp.data.source

import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.util.MainCoroutineRule
import com.google.common.truth.Truth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.jasvir.localgalleryapp.data.models.Result.Success
import com.jasvir.localgalleryapp.data.models.Result.Error
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * @author Jasvir
 */

@ExperimentalCoroutinesApi
class PicGalleryRepositoryTest {

    private val imageUri = ImageUri("Uri1")
    private val imageUri2 = ImageUri("Uri2")
    private val localImages = listOf(imageUri, imageUri2)

    private lateinit var picGalleryLocalDataSource: FakeDataSource

    private lateinit var repository: PicGalleryRepositoryImpl

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        picGalleryLocalDataSource = FakeDataSource(localImages.toMutableList())

        repository = PicGalleryRepositoryImpl(picGalleryLocalDataSource)
    }


    @Test
    fun getImages_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource()
        val tempRepository = PicGalleryRepositoryImpl(emptySource)

        assertThat(tempRepository.fetchPictures( 0, 10) is Success).isTrue()
    }

    @Test
    fun getImages_requestsImagesFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
        // When images are requested from the images repository
        val images = repository.fetchPictures(0, 30) as Success

        // Then images are loaded from the local data source
        assertThat(images.data).isEqualTo(localImages)
    }

    @Test
    fun saveImage_saveToLocal() = mainCoroutineRule.runBlockingTest {
        // When images are requested from the images repository
        val images = repository.fetchPictures(0, 30) as Success

        // Save images
        repository.savePicture("Uri3")

        // Fetch them
        val imagesLocal = repository.fetchPictures(0, 30) as Success

        // they aren't equal
        assertThat(images.data).isNotEqualTo(imagesLocal.data)

        // New data has added image
        assertThat(imagesLocal.data[2]).isEqualTo(ImageUri("Uri3"))
    }

    @Test
    fun getImages_DataSourceUnavailable_returnsError() = mainCoroutineRule.runBlockingTest {
        // When both sources are unavailable
        picGalleryLocalDataSource.images = null

        // The repository returns an error
        assertThat(repository.fetchPictures(0, 30)).isInstanceOf(Error::class.java)
    }

    @Test
    fun getImages_deleteImages() = mainCoroutineRule.runBlockingTest {
        // Get images
        val initialImages = repository.fetchPictures(0, 30) as? Success

        picGalleryLocalDataSource.deletePics()

        // Fetch after delete
        val afterDeleteImages = repository.fetchPictures(0, 30) as? Success

        //check
        assertThat(initialImages?.data).isNotEmpty()
        assertThat(afterDeleteImages?.data).isEmpty()
    }

}