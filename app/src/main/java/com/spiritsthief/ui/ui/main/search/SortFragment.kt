package com.spiritsthief.ui.ui.main.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.spiritsthief.R
import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.ui.ui.main.BaseFragment

class SortFragment: BaseFragment() {
    lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = LinearLayout(activity)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = lp

        val optionsList = mutableListOf<TextView>()
        resources.getStringArray(R.array.sorting_options).forEachIndexed { i, option ->
            val tv = inflater.inflate(R.layout.sort_option, layout, false) as TextView
            tv.text = option
            tv.setOnClickListener {
                sortBy(i)
                optionsList.forEachIndexed { index, textView ->
                    textView.isSelected = index == i
                }
            }
            layout.addView(tv)
            optionsList.add(tv)
        }

        return layout
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        searchViewModel = ViewModelProviders.of(activity!!).get(SearchViewModel::class.java)
    }

    fun sortBy(index: Int) {
        when(index) {
            0 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_NAME, "ASC")
            1 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_NAME, "DESC")
            2 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_AVG_PRICE, "ASC")
            3 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_AVG_PRICE, "DESC")
            4 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_AGE, "ASC")
            5 -> searchViewModel.searchRequest.setSort(SearchRequest.SORT_BY_AGE, "DESC")
        }

        searchViewModel.performSearch()
    }
}