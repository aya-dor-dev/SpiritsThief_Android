package com.spiritsthief.ui.ui.main

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.common.SimpleScrollListener

abstract class BaseFragment: Fragment() {
    val uiHandler = Handler(Looper.getMainLooper())
    var scrollListener: SimpleScrollListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SimpleScrollListener) {
            scrollListener = context
        }
    }

    fun attachRecyclerViewToScrollListener(rv: RecyclerView) {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    scrollListener?.onScrollDown()
                } else {
                    scrollListener?.onScrollUp()
                }
            }
        })
    }

    /**
     * @return Back press was handled
     */
    open fun onBackPressed(): Boolean = false

    fun hideSoftKeybard() {
        activity?.currentFocus?.let {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }
}