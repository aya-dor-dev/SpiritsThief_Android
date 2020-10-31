package com.spiritsthief.ui.ui.main.wishlist

import com.spiritsthief.ui.ui.main.BaseBottlesViewModel

class WishListViewModel: BaseBottlesViewModel() {
    fun performSearch(ids: List<Long>) {
        searchRequest.id = ids.toMutableList()
        count = ids.size
        performSearch()
    }
}