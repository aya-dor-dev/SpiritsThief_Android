package com.spiritsthief.viewmodel

import androidx.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.spiritsthief.api.IApi
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.Response
import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.common.AnalyticsHelper

/**
 * Created by dorayalon on 28/01/2018.
 */
class BrowseViewModel: ApiViewModel() {
    companion object {
        val TAG = "BrowseViewModel"
    }

    private lateinit var searchRequest: SearchRequest
    var data: MutableLiveData<Response<Bottle>> = MutableLiveData()
    var count: MutableLiveData<Int> = MutableLiveData()
    var task: SearchTask? = null

    init {
        count.value = -1
    }

    fun search(searchRequest: SearchRequest) {
        this.searchRequest = searchRequest
        count.value = null
        task?.cancel(true)

        task = SearchTask(api, searchRequest) {
            data.postValue(it)
        }
        task!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

        val copy = searchRequest.copy()
        copy.calculateCount = true
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            var count:Int = -1

            try {
                count = api.getBottles(copy).execute()?.body()?.meta?.count!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

            this.count.postValue(count)
        }
    }

    fun getNextPage() {
        searchRequest.increaseOffset()
        Log.v("REQ", Gson().toJson(searchRequest))
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            Log.v(TAG, "Loading more bottles")
            var res: Response<Bottle>? = null
            try {
                res = api.getBottles(searchRequest).execute()?.body()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var logMessage = ""

            when (res) {
                null -> "Failed to load more bottles"
                else -> {
                    val oldResult = data.value!!
                    oldResult.results.addAll(res.results)
                    data.postValue(oldResult)
                    logMessage = when (res.meta.count) {
                        0 -> "More bottles was loaded but is empty"
                        else -> "Successfully loaded more ${res.results.size} bottles"
                    }
                }
            }

            Log.v(TAG, logMessage)
        }
    }

    class SearchTask(val api: IApi,
                     private val searchRequest: SearchRequest,
                     private val listener: (Response<Bottle>?) -> Unit): AsyncTask<Void, Void, Response<Bottle>?>() {
        override fun doInBackground(vararg params: Void?): Response<Bottle>? {

            Log.v(TAG, "Loading list of bottles")
            var res: Response<Bottle>? = null
            try {
                res = api.getBottles(searchRequest).execute()?.body()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var logMessage = ""

            when (res) {
                null -> "Failed to load list of bottles"
                else -> {
                    logMessage = when (res.meta.count) {
                        0 -> "List of bottles was loaded but is empty"
                        else -> "Successfully loaded list of ${res.results.size} bottles"
                    }
                }
            }

            Log.v(TAG, logMessage)

            return res
        }

        override fun onPostExecute(result: Response<Bottle>?) {
            super.onPostExecute(result)
            if (!isCancelled) {
                listener.invoke(result)
            }
        }
    }

    fun hasMore() = data.value!!.results.size < count.value!!
}


