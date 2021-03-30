package com.jasvir.localgalleryapp.utils.helpers.dialogs

import com.jasvir.localgalleryapp.R


interface DialogManager {

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        textId: Int,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        text: String,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

//    fun openOneButtonDialog(
//        buttonTextId: Int = R.string.ok,
//        titleId: Int,
//        messageId: Int,
//        cancelable: Boolean = false,
//        onClickOk: (() -> Unit)? = null
//    )

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        title: String,
        message: String,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun openTwoButtonsDialog(
        titleText: String?,
        messageText: String?,
        positiveButtonName: String,
        negativeButtonName: String,
        cancelable: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null
    )

    fun dismissAll()

    fun isDialogShown() : Boolean
}