package com.felipe.appExample.ui.utils

import android.R
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object DialogUtils {

    fun buildDialog(context: Context, title: String, message: String): MaterialAlertDialogBuilder {
        val builder = MaterialAlertDialogBuilder(context)

        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(context.getString(R.string.ok), null)


        return builder

    }
}