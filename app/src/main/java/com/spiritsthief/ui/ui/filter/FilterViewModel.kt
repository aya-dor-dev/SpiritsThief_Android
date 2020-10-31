package com.spiritsthief.ui.ui.filter

import androidx.lifecycle.ViewModel
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.ApiRequest
import com.spiritsthief.api.model.CaskType
import com.spiritsthief.api.model.Category
import com.spiritsthief.api.model.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FilterViewModel : ViewModel() {
    lateinit var searchRequest: SearchRequest
    var casks: CaskType? = null

    fun clear() {
        val newSR = SearchRequest().apply {
            name = searchRequest.name
            barcode = searchRequest.barcode
            sortBy = searchRequest.sortBy
            sortOrder = searchRequest.sortOrder
        }
        searchRequest = newSR
    }

    fun getCasks(result: (CaskType) -> Unit) = GlobalScope.launch(Dispatchers.Main) {
        if (casks == null) {
            val res = try {
                ApiClient.get().getCaskTypes().await()
            } catch (e: Exception) {
                null
            }

            res?.let {
                casks = it.results[0]
            }
        }

        result(casks!!)
    }

    fun getSubCategories(result: (Category) -> Unit) = GlobalScope.launch(Dispatchers.Main) {
        val res = try {
            val sr = ApiRequest().apply {
                category = searchRequest.category
            }
            ApiClient.get().getCategories(sr).await()
        } catch (e: Exception) {
            null
        }

        var category: Category? = null
        res?.let {
            category = it.results.filter { it.name == searchRequest.category[0] }[0]
        }

        result(category!!)
    }
}