package com.spiritsthief.viewmodel

import androidx.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.Response
import com.spiritsthief.api.model.SearchRequest

/**
 * Created by Dor Ayalon on 1/5/18.
 */
class BottlesListViewModel: ApiViewModel() {
    companion object {
        val TAG = "BottlesListViewModel"
    }

    var data: MutableLiveData<Response<Bottle>> = MutableLiveData()

    fun search(searchRequest: SearchRequest) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute({
            Log.v(BottlesListViewModel.TAG, "Loading list of bottles")
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
                    data.postValue(res)
                    logMessage = when (res.meta.count) {
                        0 -> "List of bottles was loaded but is empty"
                        else -> "Successfully loaded list of ${res.meta.count} bottles"
                    }
                }
            }

            Log.v(TAG, logMessage)
        })
    }
}