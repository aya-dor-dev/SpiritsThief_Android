package com.spiritsthief.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.spiritsthief.R

/**
 * Created by dorayalon on 01/02/2018.
 */
class SquareRelativeLayout : RelativeLayout {
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

        val dimen = when (byHeight) {
            true -> measuredHeight
            false -> measuredWidth
        }
        setMeasuredDimension(dimen, dimen)
    }
}