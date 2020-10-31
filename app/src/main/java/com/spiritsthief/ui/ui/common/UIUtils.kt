package com.spiritsthief.ui.ui.common

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.spiritsthief.R

fun AppCompatActivity.showDialog(@StringRes title: Int,
               @StringRes message: Int,
               @DrawableRes icon: Int,
               @StringRes positiveAction: Int,
               @StringRes negativeAction: Int,
               onPositiveClick: () -> Unit,
               onNegativeClick: () -> Unit) {
    var dialog: AlertDialog? = null
    val view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_with_image, null)
    view.findViewById<ImageView>(R.id.icon).setImageResource(icon)
    view.findViewById<TextView>(R.id.message).setText(message)
    view.findViewById<TextView>(R.id.negative_action).apply {
        setText(negativeAction)
        setOnClickListener {
            dialog?.dismiss()
            onNegativeClick()
        }
    }

    view.findViewById<TextView>(R.id.positive_action).apply {
        setText(positiveAction)
        setOnClickListener {
            dialog?.dismiss()
            onPositiveClick()
        }
    }

    dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
}

fun AppCompatActivity.showDialog(@StringRes title: Int,
                                 @StringRes message: Int,
                                 @DrawableRes icon: Int,
                                 duration: Long) {
    var dialog: AlertDialog? = null
    val view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_with_image, null)
    view.findViewById<ImageView>(R.id.icon).setImageResource(icon)
    view.findViewById<TextView>(R.id.message).setText(message)
    view.findViewById<View>(R.id.actions).visibility = View.GONE

    dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    Handler(mainLooper).postDelayed({dialog?.dismiss()}, duration)
}