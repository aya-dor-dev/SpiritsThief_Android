package com.spiritsthief.viewmodel

import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.ShopsRequest
import com.spiritsthief.api.model.SortedStores
import com.spiritsthief.api.model.Store
import io.fabric.sdk.android.services.concurrency.AsyncTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Dor Ayalon on 1/12/18.
 */
class ShopLinksViewModel: ApiViewModel() {
    companion object {
        val TAG = "ShopLinksViewModel"
    }
    var shops: MutableLiveData<SortedStores> = MutableLiveData()

    fun getStoresForBottleId(bottleId: Long) = GlobalScope.launch(Dispatchers.Main) {
        val request = ShopsRequest(bottleId).apply { sortBy = "price" }
        val apiRequest = ApiClient.get().getShopsForBottle(request)
        val res = try {
            apiRequest!!.await()
        } catch (e: Exception) {
            null
        }

        when (res) {
            null -> {}
            else -> {
                shops.postValue(SortedStores(res))
            }
        }
    }

//    fun getStoresForBottleId(bottleId: Long) {
//        AsyncTask.THREAD_POOL_EXECUTOR.execute {
//            Log.v(TAG, "Loading list of stores")
//            var list: List<Store>? = null
//
//            try {
//                val request = ShopsRequest(bottleId).apply {
//                    sortBy = "price"
//                }
//                list = api.getShopsForBottle(request).execute().body()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            var logMessage = when (list) {
//                null -> "Failed to load list of stores"
//                else -> {
//                    shops.postValue(SortedStores(list))
//                    when (list.size) {
//                        0 -> "List of stores was loaded but is empty"
//                        else -> "Successfully loaded list of ${list.size} stores"
//                    }
//                }
//            }
//            Log.v(TAG,logMessage)
//        }
//    }
}