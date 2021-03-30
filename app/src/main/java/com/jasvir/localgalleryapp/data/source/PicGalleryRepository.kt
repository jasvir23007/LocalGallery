package com.jasvir.localgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.data.models.Result

/**
 * @author Jasvir
 */
interface PicGalleryRepository {

    fun observePictures(page: Int) : LiveData<Result<List<ImageUri>>>

    suspend fun savePicture(uri: String)

    suspend fun fetchPictures(page: Int, per_page: Int) : Result<List<ImageUri>>

    suspend fun deletePics()
}