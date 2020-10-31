package com.spiritsthief.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.spiritsthief.R

/**
 * Created by dorayalon on 28/01/2018.
 */
class CustomCheckBox : RelativeLayout {
    val text: TextView
//    val icon: ImageView
    val check: ImageView
    private var isChecked = false

    var onCheckChanged: (Boolean) -> Unit? = {}

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.custom_check_bx, this, true)
        text = findViewById(R.id.text)
        check = findViewById(R.id.check)

        setOnClickListener {
            setIsChecked(!isChecked)
        }
    }

    fun setIsChecked(checked: Boolean) {
        this.isChecked = checked
        check.visibility = if (isChecked) View.VISIBLE else View.GONE
        onCheckChanged.invoke(isChecked)
    }
}