package com.jasvir.localgalleryapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.data.source.PicGalleryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.jasvir.localgalleryapp.data.models.Result
import com.jasvir.localgalleryapp.data.models.Result.Success
import com.jasvir.localgalleryapp.data.models.Result.Error
import java.lang.Exception

/**
 * @author Jasvir
 */
class PicGalleryLocalDataSource(
    private val dao: PicGalleryDAO,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : PicGalleryDataSource {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {
        return dao.observeUris(page, 50).map { it -> Success(it) }
    }

    override suspend fun savePicture(uri: String) = withContext(dispatchers) {
        dao.savePic(ImageUri(uri))
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> =
        withContext(dispatchers) {
            return@withContext try {
                Success(dao.fetchUris())
            } catch (e: Exception){
                Error(e)
            }
        }

    override suspend fun deletePics() =
        withContext(dispatchers) {
            dao.deletePictures()
    }
}