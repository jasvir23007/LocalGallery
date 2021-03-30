package com.jasvir.localgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.data.models.Result
import java.lang.Exception

/**
 * @author Jasvir
 */
class FakeDataSource(
    var images: MutableList<ImageUri>? = mutableListOf()
) : PicGalleryDataSource {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {
        TODO("Not yet implemented")
    }

    override suspend fun savePicture(uri: String) {
        images?.add(ImageUri(uri))
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> {
        images?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(Exception("Images not found"))
    }

    override suspend fun deletePics() {
        images?.clear()
    }
}