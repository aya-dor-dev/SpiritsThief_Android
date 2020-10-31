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
class DrawerItem : LinearLayout {
    val title: TextView
    val subtitle: TextView
    val icon: ImageView


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        val titleText = a.getString(R.styleable.TileView_title)
        val dataText = a.getString(R.styleable.TileView_data)
        val iconRes = a.getDrawable(R.styleable.TileView_icon)
        val singleLine = a.getBoolean(R.styleable.TileView_single_line, false)
        val iconTint = a.getColor(R.styleable.TileView_icon_tint, 0)
                a.recycle()

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.drawer_item, this, true)
        subtitle = findViewById(R.id.subtitle)
        title = findViewById(R.id.title)
        icon = findViewById(R.id.icon)


        if (singleLine) subtitle.visibility = View.GONE
        if (iconRes != null) icon.setImageDrawable(iconRes)
        if (iconTint != 0) ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(iconTint))

        title.text = titleText
        subtitle.text = dataText

        title.setOnClickListener {
            this@DrawerItem.performClick()
        }

        subtitle.setOnClickListener {
            this@DrawerItem.performClick()
        }
    }
}