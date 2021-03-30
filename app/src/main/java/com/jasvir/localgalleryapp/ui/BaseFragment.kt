package com.jasvir.localgalleryapp.ui


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jasvir.localgalleryapp.R
import com.jasvir.localgalleryapp.utils.helpers.dialogs.DialogManager
import com.jasvir.localgalleryapp.utils.helpers.observe
import org.koin.core.parameter.parametersOf
import org.koin.android.ext.android.get

/**
 * @author Jasvir
 */
abstract class BaseFragment : Fragment() {

    private var dialogManager: DialogManager? = null

    protected val CAMERA_PERMISSION_CODE = 12345

    override fun onStop() {
        super.onStop()
        dialogManager?.dismissAll()
    }

    protected open fun observeError(errorLiveData: LiveData<Throwable>) {
        errorLiveData.observe(this) {
            showError(it ?: return@observe)
        }
    }

    protected open fun observeErrorRefreshLayout(errorLiveData: LiveData<Throwable>, swipeRefreshLayout: SwipeRefreshLayout) {
        errorLiveData.observe(this) {
            showError(it ?: return@observe)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    protected open fun showError(throwable: Throwable) {
        if (throwable is IllegalArgumentException){
            showError(getString(R.string.inside_image))
        } else if (throwable is Exception) {
            showError(throwable.localizedMessage)
        } else
            showUnknownError()
    }

    protected open fun showError(error: String?) {
        if (error != null) {
            getDialogManager().openOneButtonDialog(R.string.ok, error, true)
        } else {
            showUnknownError()
        }
    }

    protected open fun showUnknownError() {
        getDialogManager().openOneButtonDialog(R.string.ok, R.string.error_default, true)
    }

    protected open fun showPermissionDialog() {
        getDialogManager().openOneButtonDialog(R.string.ok, getString(R.string.camera_permission),
            getString(R.string.permit_warning),
            true,
            onClickOk = {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            })
    }

    protected open fun showSecondPermissionDialog() {

        getDialogManager().openTwoButtonsDialog(getString(R.string.camera_permission),
            getString(R.string.permit_warning_2),
            getString(R.string.ok),
            getString(R.string.cancel),
            onPositiveButtonClick = {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                startActivity(intent)
            },
            onNegativeButtonClick = {
                removeDialogs()
            }
        )
    }

    private fun getDialogManager(): DialogManager {
        if (dialogManager == null) {
            dialogManager = get { parametersOf(requireContext()) }
        }

        return dialogManager!!
    }

    private fun removeDialogs() {
        dialogManager?.dismissAll()
    }


    // this is called when user closes the permission request dialog


    fun permissionGranted() = ContextCompat.checkSelfPermission(
        activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
