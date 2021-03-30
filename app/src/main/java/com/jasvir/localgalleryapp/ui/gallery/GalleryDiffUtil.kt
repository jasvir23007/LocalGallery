package com.jasvir.localgalleryapp.ui.gallery

import androidx.recyclerview.widget.DiffUtil
import com.jasvir.localgalleryapp.data.models.ImageUri

/**
 * @author Jasvir
 */
class GalleryDiffUtil: DiffUtil.ItemCallback<ImageUri>() {
    override fun areContentsTheSame(oldItem: ImageUri, newItem: ImageUri): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areItemsTheSame(oldItem: ImageUri, newItem: ImageUri): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}