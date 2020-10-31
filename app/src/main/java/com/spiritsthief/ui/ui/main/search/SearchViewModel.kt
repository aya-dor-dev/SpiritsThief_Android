package com.spiritsthief.ui.ui.main.search

import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.ui.ui.main.BaseBottlesViewModel
import com.spiritsthief.ui.ui.main.STATUS


class SearchViewModel: BaseBottlesViewModel() {
    init {
        searchRequest.category = arrayListOf("Whisky")
    }

    override fun performSearch() {
        super.performSearch()
        getCount()
    }

    override fun clear() {
        super.clear()
        count = null
    }

    fun performBarcodeSearch(barcode: String) {
        countApiRequest?.cancel()
        searchApiRequest?.cancel()
        clear()
        status.postValue(STATUS.IN_PROGRESS)
        searchRequest = SearchRequest().apply {
            this.barcode = barcode
        }
        getCount()
        loadBottles()
    }
}