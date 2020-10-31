package com.spiritsthief.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.spiritsthief.R
import android.widget.RelativeLayout




/**
 * Created by Dor Ayalon on 1/8/18.
 */
class FlatTextualTile : LinearLayout {
    val title: TextView
    val text: TextView
    val editable: EditText
    var square: Boolean = false
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        val titleText = a.getString(R.styleable.TileView_title)
        val dataText = a.getString(R.styleable.TileView_data)
        square = a.getBoolean(R.styleable.TileView_square, false)
        val isEditable = a.getBoolean(R.styleable.TileView_editable, false)
        a.recycle()

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.flat_textual_tile, this, true)
        text = findViewById(R.id.data)
        title = findViewById(R.id.title)
        editable = findViewById(R.id.editable_data)
        title.text = titleText
        text.text = dataText

        when (isEditable) {
            true -> {
                editable.visibility = View.VISIBLE
                text.visibility - View.GONE
            }
            false -> {
                editable.visibility = View.GONE
                text.visibility - View.VISIBLE
            }
        }

        if (TextUtils.isEmpty(titleText)) {
            title.visibility = View.GONE
            val layoutParams = text.layoutParams as RelativeLayout.LayoutParams
            layoutParams.removeRule(RelativeLayout.ABOVE)
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            text.layoutParams = layoutParams
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (square) {
            val width = measuredWidth
            setMeasuredDimension(width, width)
        }
    }
}
