package com.jasvir.localgalleryapp.utils.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File

/**
 * @author Jasvir
 */

class ImageHelper {
    companion object {
        fun resizeImage(file: File, scaleTo: Int): Bitmap {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            val temp = BitmapFactory.decodeFile(file.absolutePath, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / scaleTo, photoH / scaleTo)

            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor

            val resized = BitmapFactory.decodeFile(file.absolutePath, bmOptions) ?: return temp
            file.outputStream().use {
                resized.compress(Bitmap.CompressFormat.JPEG, 90, it)
//                resized.recycle()
                return resized
            }
        }

        fun setOrientation(b: Bitmap, orientation: Int): Bitmap? {
            if (orientation > 0) {
                val matrix = Matrix()
                matrix.setRotate(orientation.toFloat())
                return Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, false)
            } else {
                return b
            }
        }

        fun saveBitmapToFile(){

        }

    }
}