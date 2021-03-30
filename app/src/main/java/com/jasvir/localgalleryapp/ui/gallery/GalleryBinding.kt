package com.jasvir.localgalleryapp.ui.gallery

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jasvir.localgalleryapp.data.models.ImageUri

/**
 * @author Jasvir
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<ImageUri>?) {
    if(items == null) return

    (listView.adapter as GalleryAdapter).submitList(items)
}

@BindingAdapter("app:imageSourceGallery")
fun setImagePreview(imageView: ImageView, uri: String){

    try {
        imageView.setImageURI(Uri.parse(uri))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}