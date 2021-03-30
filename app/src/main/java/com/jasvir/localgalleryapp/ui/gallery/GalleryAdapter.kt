package com.jasvir.localgalleryapp.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jasvir.localgalleryapp.data.models.ImageUri
import com.jasvir.localgalleryapp.databinding.ItemGalleryBinding
import kotlinx.android.extensions.LayoutContainer

/**
 * @author Jasvir
 */
class GalleryAdapter(
    private val galleryViewModel: GalleryViewModel): ListAdapter<ImageUri, GalleryImageViewHolder>(GalleryDiffUtil()) {

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()
    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: GalleryImageViewHolder, position: Int) {
        holder.bind(galleryViewModel, getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImageViewHolder {
        return GalleryImageViewHolder.from(parent)
    }

}

class GalleryImageViewHolder private constructor(val binding: ItemGalleryBinding) :
    RecyclerView.ViewHolder(binding.root), LayoutContainer {

    override val containerView = binding.root

    companion object {
        fun from(parent: ViewGroup): GalleryImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemGalleryBinding.inflate(layoutInflater, parent, false)

            return GalleryImageViewHolder(binding)
        }
    }

    fun bind(galleryViewModel: GalleryViewModel, uri: ImageUri, position: Int){
        binding.viewModel = galleryViewModel
        binding.imageUri = uri
        binding.position = position
        binding.executePendingBindings()
    }
}

