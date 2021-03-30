package com.jasvir.localgalleryapp.ui.camera

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult

/**
 * @author Jasvir
 */
@BindingAdapter("app:imageSource")
fun setImagePreview(imageView: ImageView, image: Bitmap?){

    try {
        imageView.setImageBitmap(image)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("app:onPhotoTaken")
fun onPhotoTaken(cameraView: CameraView, photoListener: PhotoListener) {

    cameraView.addCameraListener(object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            photoListener.photoTaken(result)
        }
    })
}

interface PhotoListener {
    fun photoTaken(result: PictureResult)
}