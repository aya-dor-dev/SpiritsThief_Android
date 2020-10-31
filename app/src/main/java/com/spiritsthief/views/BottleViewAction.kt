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
class BottleViewAction : LinearLayout {
    val title: TextView
    val icon: ImageView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        val titleText = a.getString(R.styleable.TileView_title)
        val iconRes = a.getDrawable(R.styleable.TileView_icon)

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.bottle_view_action, this, true)
        title = findViewById(R.id.title)
        icon = findViewById(R.id.icon)

        if (iconRes != null) icon.setImageDrawable(iconRes)

        title.text = titleText

        title.setOnClickListener {
            this@BottleViewAction.performClick()
        }
    }
}