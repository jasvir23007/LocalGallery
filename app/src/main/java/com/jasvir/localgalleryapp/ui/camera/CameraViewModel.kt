package com.jasvir.localgalleryapp.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.jasvir.localgalleryapp.data.source.PicGalleryRepository
import com.jasvir.localgalleryapp.utils.SingleLiveEvent
import com.jasvir.localgalleryapp.utils.helpers.ImageHelper
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


/**
 * @author Jasvir
 */
class CameraViewModel(
    private val context: Context,
    private val repository: PicGalleryRepository) : ViewModel(){

    val isCameraVisible = ObservableField(true)
    val photo = ObservableField<Bitmap>()
    val photoFile = ObservableField<File>()

    val takePhoto = SingleLiveEvent<Unit>()
    val photoSaved = SingleLiveEvent<String>()
    val photoCropped = SingleLiveEvent<IntArray>()

    var cropFrame = IntArray(4)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private var modifyMode = false

    fun photoTaken(image: PictureResult) {
        isCameraVisible.set(false)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(context).downloadOnly().load(image.data).submit().get()
                val bitmap = ImageHelper.resizeImage(file, 512)
                photoFile.set(file)
                photo.set(ImageHelper.setOrientation(bitmap, 90))
                photoCropped.postValue(intArrayOf(bitmap.height, bitmap.width))
                isCameraVisible.set(false)
            } catch (e: Exception) {
                _error.postValue(e)
                e.printStackTrace()
            }
        }
    }

    fun photoOpened(imageUri: String) {
        isCameraVisible.set(false)
        modifyMode = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(imageUri)
                val bitmap = ImageHelper.resizeImage(file, 512)
                photoFile.set(file)
                photo.set(bitmap)
                photoCropped.postValue(intArrayOf(bitmap.width, bitmap.height))
            } catch (e: Exception) {
                _error.postValue(e)
                e.printStackTrace()
            }
        }
    }

    fun takePhoto(){
        takePhoto.call()
    }

    fun saveRetake(save: Boolean) {

        if(!modifyMode)
            isCameraVisible.set(!save)

        if (save)
            viewModelScope.launch {
                try {
                    photoFile.get()?.outputStream().use {
                        photo.get()?.compress(Bitmap.CompressFormat.JPEG, 90, it)
                    }
                    repository.savePicture(photoFile.get().toString())
                    photoFile.set(null)
                    photo.set(null)
                    photoSaved.postValue("")
                } catch (e: Exception) {
                    _error.postValue(e)
                    e.printStackTrace()
                }
            }
        else if(!modifyMode && !save) {
            if(photoFile.get()?.exists()!!) {
                photoFile.get()!!.delete()
                photoFile.set(null)
            }
        }else if(modifyMode && !save)
            photoSaved.postValue("")

    }

    fun rotateImage() {
        try {
            photo.get()?.let {
                val bitmap = ImageHelper.setOrientation(it, 90)
                photo.set(bitmap)
                photoCropped.postValue(intArrayOf(bitmap!!.width, bitmap.height))
            }
        } catch (e: Exception) {
            _error.postValue(e)
            e.printStackTrace()
        }
    }

    fun crop(){
        viewModelScope.launch {
            try {
                var temp = 0
                if(cropFrame[0] > cropFrame[1]){
                    temp = cropFrame[0]
                    cropFrame[0] = cropFrame[1]
                    cropFrame[1] = temp
                }
                if(cropFrame[2] > cropFrame[3]){
                    temp = cropFrame[2]
                    cropFrame[2] = cropFrame[3]
                    cropFrame[3] = temp
                }
                val cropWidth = cropFrame[1] - cropFrame[0]
                val cropHeight = cropFrame[3] - cropFrame[2]
                val croppedBitmap = Bitmap.createBitmap(
                    photo.get()!!,
                    cropFrame[0],
                    cropFrame[2],
                    cropWidth,
                    cropHeight
                )

                photo.set(croppedBitmap)
                photoCropped.postValue(intArrayOf(cropWidth, cropHeight))

            } catch (e: Exception) {
                _error.postValue(e)
                e.printStackTrace()
            }
        }
    }
}