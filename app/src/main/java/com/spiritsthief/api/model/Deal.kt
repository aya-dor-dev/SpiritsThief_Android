package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Dor Ayalon on 1/15/18.
 */
class Deal: Bottle() {
        @SerializedName("discount")
        val discount: String = ""
        @SerializedName("deal_price")
        val newPrice: Double = 0.0
        @SerializedName("regular_price")
        val oldPrice: Double = 0.0
        @SerializedName("url")
        val link: String = ""
        val storeImageUrl: String = ""
        @SerializedName("store_name")
        val storeName: String = ""
}