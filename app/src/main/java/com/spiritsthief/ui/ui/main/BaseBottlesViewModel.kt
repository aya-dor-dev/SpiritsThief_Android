package com.spiritsthief.ui.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.Demo
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.Response
import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.common.AnalyticsHelper
import com.spiritsthief.ui.adapter.PagedListHandler
import kotlinx.coroutines.*

enum class STATUS {
    FINISHED,
    IN_PROGRESS,
    ERROR
}

abstract class BaseBottlesViewModel : ViewModel(), PagedListHandler {
    protected var searchApiRequest: Deferred<Response<Bottle>>? = null
    protected var countApiRequest: Deferred<Response<Bottle>>? = null

    val status = MutableLiveData<STATUS>()
    var bottles: ArrayList<Bottle>? = null
    var count: Int? = null

    var searchRequest = SearchRequest()

    open fun clear() {
        searchRequest.offset = 0
        bottles = null
    }

    open fun performSearch() {
        countApiRequest?.cancel()
        searchApiRequest?.cancel()
        status.postValue(STATUS.IN_PROGRESS)
        loadBottles()
    }

    protected fun loadBottles() = GlobalScope.launch(Dispatchers.Main) {
        AnalyticsHelper.performSearch(searchRequest)
        searchApiRequest = ApiClient.get().getBottles2(searchRequest)
        val res = try {
            searchApiRequest!!.await()
        } catch (e: Exception) {
            null
        }

        when (res) {
            null -> if (!searchApiRequest?.isCancelled!!) {status.postValue(STATUS.ERROR) }
            else -> {
                val list = bottles ?: arrayListOf()
                val newList = res.results
                newList.forEach {
                    val images = it.imageUrl.filter { it.trim().startsWith("http") }.map { it.trim() }
                    it.imageUrl.clear()
                    it.imageUrl.addAll(images)
                }
                list.addAll(res.results)
                bottles = list

                pagingPatch = res.results.size == 0
            }
        }

        onResults()
    }

    protected fun getCount() = GlobalScope.launch(Dispatchers.Main) {
        val sr = searchRequest.copy()
        sr.calculateCount = true
        countApiRequest = ApiClient.get().getBottles2(sr)

        val res = try {
            countApiRequest!!.await()
        } catch (e: Exception) {
            null
        }

        when (res) {
            null -> if (!countApiRequest?.isCancelled!!) {
                status.postValue(STATUS.ERROR)
            }
            else -> count = res.meta.count
        }

//        count = 20

        onResults()
    }

    private fun onResults() {
        if (count != null && bottles != null) {
            AnalyticsHelper.searchResultsReceived(searchRequest, count!!)
            status.postValue(STATUS.FINISHED)
        }
    }

    var pagingPatch = false

    override fun hasMore() = !pagingPatch && bottles!!.size < count!!

    override fun loadMode() {
        if (searchApiRequest!!.isCompleted) {
            searchRequest.offset += 20
            loadBottles()
        }
    }
}