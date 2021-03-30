package com.jasvir.localgalleryapp.ui.camera

import android.graphics.Rect
import androidx.navigation.fragment.navArgs
import com.jasvir.localgalleryapp.R
import com.jasvir.localgalleryapp.databinding.FragmentCameraBinding
import com.jasvir.localgalleryapp.ui.BindingFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.jasvir.localgalleryapp.utils.helpers.observe

/**
 * @author Jasvir
 */
class CameraFragment : BindingFragment<FragmentCameraBinding>() {

    override val layoutId = R.layout.fragment_camera

    private val viewModel: CameraViewModel by viewModel()

    private val args: CameraFragmentArgs by navArgs()

    override fun onViewCreated() {
        super.onViewCreated()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if(!args.imageUri.isNullOrEmpty()){
            viewModel.photoOpened(args.imageUri!!)
        }

        setupObservers()
        setupCropFrame()
    }

    override fun onResume() {
        super.onResume()
        if (cameraFragmentCamera != null && args.imageUri.isNullOrEmpty())
            cameraFragmentCamera.open()
    }

    override fun onPause() {
        super.onPause()
        if (cameraFragmentCamera != null  && args.imageUri.isNullOrEmpty())
            cameraFragmentCamera.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraFragmentCamera != null  && args.imageUri.isNullOrEmpty())
            cameraFragmentCamera.destroy()
    }

    private fun setupCropFrame() {
        dragRect.setOnUpCallback(object : DragRectView.OnUpCallback {
            override fun onRectFinished(rect: Rect?, coors: IntArray) {
                viewModel.cropFrame = coors
            }
        })
    }

    private fun setupObservers() {

        observeError(viewModel.error)

        viewModel.takePhoto.observe(this) {
            cameraFragmentCamera.takePicture()
        }

        viewModel.photoSaved.observe(this) {
            onBackPressed()
        }

        viewModel.photoCropped.observe(this) {
            it?.let {  dragRect.drawEdges(it[0], it[1]) }
        }

        viewModel.loading.observe(this) {
            it?.let {
                galleryFragSwipeLayout.isEnabled = it
                galleryFragSwipeLayout.isRefreshing = it
            }
        }
    }

    private fun onBackPressed() {
        activity?.onBackPressed()
    }
}
