package com.jasvir.localgalleryapp.ui.gallery

import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.data.models.Result
import com.jasvir.localgalleryapp.data.source.PicGalleryRepository
import com.jasvir.localgalleryapp.utils.SingleLiveEvent
import com.jasvir.localgalleryapp.data.models.Result.Success
import com.jasvir.localgalleryapp.utils.helpers.ImageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @author Jasvir
 */
class GalleryViewModel(
    private val context: Context,
    private val repository: PicGalleryRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _page = MutableLiveData<Int>(0)

    val imageClick = SingleLiveEvent<String>()

    private val _items: LiveData<List<ImageUri>> =
        _page.switchMap { page ->
            repository.observePictures(page).map {
                if (it is Success) {
                    _dataLoading.value = false
                    it.data
                } else if(it is Result.Error){
                    _dataLoading.value = false
                    _error.postValue(it.exception)
                     emptyList()
                }
                else
                    emptyList()

            }
        }

    val items: LiveData<List<ImageUri>> = _items

    fun refresh() {
        _dataLoading.postValue(true)
        _page.postValue(0)
    }

    fun deleteImages(){
        viewModelScope.launch {
            repository.deletePics()
            Glide.get(context).clearMemory()
            refresh()
        }
    }

    fun handleGalleryPic(imageData: Intent){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(context).downloadOnly().load(imageData.data)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .submit().get()
                ImageHelper.resizeImage(file, 512)
                repository.savePicture(file.toString())
            } catch (e: Exception) {
                Result.Error(e)
                e.printStackTrace()
            }
        }
    }

    fun imageClick(position: Int, uri: String){
        imageClick.postValue(uri)
    }

}