package com.spiritsthief.views

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.spiritsthief.R

/**
 * Created by Dor Ayalon on 12/18/17.
 */
class SquareImageView : ImageView {
    var byHeight: Boolean = false
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        byHeight = a.getBoolean(R.styleable.TileView_by_height, false)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val dimen = when(byHeight) {
            true -> measuredHeight
            false -> measuredWidth
        }
        setMeasuredDimension(dimen, dimen)
    }
}

class SquareCardView : CardView {
    var byHeight: Boolean = false
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView, 0, 0)
        byHeight = a.getBoolean(R.styleable.TileView_by_height, false)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val dimen = when(byHeight) {
            true -> measuredHeight
            false -> measuredWidth
        }
        setMeasuredDimension(dimen, dimen)
    }
}