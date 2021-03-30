package com.jasvir.localgalleryapp.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jasvir.localgalleryapp.R
import com.jasvir.localgalleryapp.databinding.FragmentGalleryBinding
import com.jasvir.localgalleryapp.ui.BindingFragment
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


/**
 * @author Jasvir
 */
class GalleryFragment : BindingFragment<FragmentGalleryBinding>() {

    override val layoutId = R.layout.fragment_gallery

    private val viewModel: GalleryViewModel by viewModel()

    private lateinit var galleryAdapter: GalleryAdapter

    private val SELECT_PICTURE = 1234

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupObservers()
        setupRV()
        setupListeners()

    }

    override fun onResume() {
        super.onResume()

        viewModel.refresh()

        viewModel.imageClick.observe(this){
            it?.let {  openCamera(it) }
        }
    }

    private fun setupObservers() {

        observeError(viewModel.error)
        observeErrorRefreshLayout(viewModel.error, galleryFragSwipeLayout)

    }

    private fun setupRV(){
        galleryAdapter = GalleryAdapter(viewModel)

        with(galleryFragRv) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = galleryAdapter

            // For testing
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false


            // Set the number of offscreen views to retain before adding them
            // to the potentially shared recycled view pool
            setItemViewCacheSize(100)
        }

    }

    private fun setupListeners() {

        galleryFragOptionsAddPhoto.setOnClickListener { openCamera("") }
        galleryFragOptionsAddFromRoll.setOnClickListener { openChooser() }
    }

    private fun openCamera(imageUri: String){

        if (imageUri != "") {
            val nc = NavHostFragment.findNavController(this)
            nc.navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment(imageUri))
        } else activity?.let {
            if (!permissionGranted()) {
                showPermissionDialog()
            } else {
                val nc = NavHostFragment.findNavController(this)
                nc.navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment(imageUri))
            }
        }
    }

    private fun openChooser(){
        val chooseFile =  Intent()
        chooseFile.action = Intent.ACTION_GET_CONTENT
        chooseFile.type = "image/*"
        startActivityForResult(Intent.createChooser(chooseFile, "Select picture"), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    data?.let { viewModel.handleGalleryPic(it) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (permissions[0]  == Manifest.permission.CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera("")
            }
            else{
                showSecondPermissionDialog()
            }
        }
    }
}
