package com.abdulahad.bookravulk.common

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface

fun Activity.showAlert(title: String, message: String){
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ок", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()

    }

