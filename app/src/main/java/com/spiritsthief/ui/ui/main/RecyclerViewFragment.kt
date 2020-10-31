package com.spiritsthief.ui.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.spiritsthief.R
import kotlinx.android.synthetic.main.recycler_view_fragment.*

abstract class RecyclerViewFragment: BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.recycler_view_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachRecyclerViewToScrollListener(recycler_view)
    }

    fun setTitle(@StringRes title: Int) {
        setTitle(getString(title))
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }
}