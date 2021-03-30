package com.jasvir.localgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.utils.wrapEspressoIdlingResource
import com.jasvir.localgalleryapp.data.models.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * @author Jasvir
 */
class PicGalleryRepositoryImpl(
    private val localDataSource: PicGalleryDataSource
) : PicGalleryRepository {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> =
        wrapEspressoIdlingResource {
            return localDataSource.observePictures(page)
        }


    override suspend fun savePicture(uri: String) {
        coroutineScope {
            try {
                launch { localDataSource.savePicture(uri) }
            } catch (e: Exception) {
                throw e
            }
        }
    }


    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> =
        wrapEspressoIdlingResource {
             return localDataSource.fetchPictures(page, per_page)
        }

    override suspend fun deletePics() {
        wrapEspressoIdlingResource { localDataSource.deletePics() }
    }
}