package com.jasvir.localgalleryapp.utils.helpers.dialogs


import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

class DialogManagerImpl(val activity: FragmentActivity) : DialogManager {


    private val showedDialog = LinkedHashSet<Dialog>()


    override fun openOneButtonDialog(
        buttonTextId: Int,
        text: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialogInternal(buttonTextId, null, text, cancelable, onClickOk)
    }

    override fun openOneButtonDialog(
        buttonTextId: Int,
        textId: Int,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialog(buttonTextId, activity.getString(textId), cancelable, onClickOk)
    }

    override fun openOneButtonDialog(
        buttonTextId: Int,
        title: String,
        message: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialogInternal(buttonTextId, title, message, cancelable, onClickOk)
    }

    override fun openTwoButtonsDialog(
        titleText: String?,
        messageText: String?,
        positiveButtonName: String,
        negativeButtonName: String,
        cancelable: Boolean,
        onPositiveButtonClick: (() -> Unit)?,
        onNegativeButtonClick: (() -> Unit)?
    ) {
        val builder = AlertDialog.Builder(activity)
            .setPositiveButton(positiveButtonName) { dialog, _ ->
                dialog.dismiss()
                onPositiveButtonClick?.invoke()
            }
            .setNegativeButton(negativeButtonName) { dialog, _ ->
                dialog.dismiss()
                onNegativeButtonClick?.invoke()
            }
            .setCancelable(cancelable)
        titleText?.let { builder.setTitle(it) }
        messageText?.let { builder.setMessage(it) }
        val dialog = builder.create()

        putToShowedDialogsList(dialog)
        dialog.show()
    }

    private fun openOneButtonDialogInternal(
        buttonTextId: Int,
        title: String?,
        message: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        val dialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(activity.getString(buttonTextId)) { dialog, _ ->
                dialog.dismiss()
                onClickOk?.invoke()
            }
            .setCancelable(cancelable)
            .create()

        putToShowedDialogsList(dialog)
        dialog.show()
    }

    override fun dismissAll() {
        for (dialog in showedDialog) dialog.dismiss()
    }

    override fun isDialogShown() : Boolean {
        for (dialog in showedDialog) if (dialog.isShowing) return true
        return false
    }


    private fun putToShowedDialogsList(dialog: Dialog) {
        showedDialog.add(dialog)
    }
}