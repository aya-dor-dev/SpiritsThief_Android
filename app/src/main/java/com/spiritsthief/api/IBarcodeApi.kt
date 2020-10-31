package com.spiritsthief.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IBarcodeApi {
    @GET("lookup")
    fun getMissingBarcode(@Query("upc") ids: String): Call<BarcodeResponse>
}

class BarcodeResponse {
    var items: List<BarcodeItem>? = null
}

class BarcodeItem {
    @SerializedName("title")
    var name: String? = null
}