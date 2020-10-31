package com.spiritsthief.ui.onboarding

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.spiritsthief.R

class AvailabiliyCard : FrameLayout {
    val icon: ImageView
    val title: TextView
    val message: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        val titleText = a.getString(R.styleable.TileView_title)
        val messageText = a.getString(R.styleable.TileView_data)
        val iconDrawable = a.getDrawable(R.styleable.TileView_icon)
        a.recycle()

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.onboarding_card, this, true)
        title = findViewById(R.id.title)
        message = findViewById(R.id.message)
        icon = findViewById(R.id.icon)

        title.text = titleText
        message.text = messageText
        icon.setImageDrawable(iconDrawable)
    }
}