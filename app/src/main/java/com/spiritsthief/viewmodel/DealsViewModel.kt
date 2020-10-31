//package com.spiritsthief.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import android.util.Log
//import com.spiritsthief.api.ApiRequest
//import com.spiritsthief.api.model.Deal
//import com.spiritsthief.api.model.Response
//import io.fabric.sdk.android.services.concurrency.AsyncTask
//
///**
// * Created by Dor Ayalon on 1/15/18.
// */
//class DealsViewModel: ApiViewModel() {
//    companion object {
//        val TAG = "DealsViewModel"
//    }
//    var deals: MutableLiveData<Response<Deal>> = MutableLiveData()
//
//    init {
//        AsyncTask.THREAD_POOL_EXECUTOR.execute({
//            Log.v(TAG, "Loading list of deals")
//            var res: Response<Deal>? = null
//            try {
//                res = api.getDeals(ApiRequest.Builder().sortedBy("discount", ApiRequest.SORT_DESC).build()).execute().body()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            var logMessage: String = ""
//            logMessage = when (res) {
//                null -> {
//                    "Failed to load list of deals"
//                }
//                else -> {
//                    when (res.meta.count) {
//                        0 -> "List of deals was loaded but is empty"
//                        else -> "Successfully loaded list of ${res.meta.count} deals"
//                    }
//                }
//            }
//
//            deals.postValue(res)
//            Log.v(TAG,logMessage)
//        })
//    }
//}