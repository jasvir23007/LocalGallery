package com.jasvir.localgalleryapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.data.models.Result
import kotlinx.coroutines.runBlocking

/**
 * @author Jasvir
 */
class FakeRepository : PicGalleryRepository {

    var currentListPics: List<ImageUri> = mutableListOf()

    private var shouldReturnError = false

    private val observableImages = MutableLiveData<Result<List<ImageUri>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {

        runBlocking {
            if (shouldReturnError) {
                observableImages.value = Result.Error(Exception("Test exception"))
            } else
                observableImages.value = Result.Success(currentListPics)
        }
        return observableImages
    }

    override suspend fun savePicture(uri: String) {
        if(!(currentListPics as ArrayList).contains(ImageUri((uri))))
            (currentListPics as ArrayList).add(ImageUri((uri)))
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }

        return Result.Success(currentListPics)
    }

    override suspend fun deletePics() {
        (currentListPics as ArrayList).clear()
    }
}