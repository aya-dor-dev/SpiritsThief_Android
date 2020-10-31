package com.spiritsthief.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.spiritsthief.R

/**
 * Created by dorayalon on 01/02/2018.
 */
class BottleDataRecord : LinearLayout {
    val title: TextView
    val subtitle: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context,
                title: String,
                info: String) : this(context, null) {
        this.title.text = title
        this.subtitle.text = info
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        val titleText = a.getString(R.styleable.TileView_title)
        val subtitleText = a.getString(R.styleable.TileView_data)
        a.recycle()

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.bottle_data_record, this, true)
        subtitle = findViewById(R.id.subtitle)
        title = findViewById(R.id.title)

        title.text = titleText
        subtitle.text = subtitleText

        title.setOnClickListener {
            this.performClick()
        }

        subtitle.setOnClickListener {
            this.performClick()
        }
    }
}