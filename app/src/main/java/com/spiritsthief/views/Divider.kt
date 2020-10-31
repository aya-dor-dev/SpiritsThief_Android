package com.spiritsthief.views

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.widget.ImageViewCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.spiritsthief.R

/**
 * Created by dorayalon on 25/01/2018.
 */
class Divider : LinearLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.divider, this, true)
    }
}