package com.spiritsthief.ui.ui.filter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

abstract class BaseFilterFragment: Fragment() {
    lateinit var filterViewModel: FilterViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        filterViewModel = ViewModelProviders.of(activity!!).get(FilterViewModel::class.java)
    }
}