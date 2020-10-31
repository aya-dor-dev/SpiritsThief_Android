package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Dor Ayalon on 12/18/17.
 */
data class Store(
        @SerializedName("price")
        val price: String,
        @SerializedName("currency")
        val currency: String,
        @SerializedName("product_id")
        val productId: Long,
        @SerializedName("store_id")
        val storeId: Long,
        @SerializedName("url")
        val url: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("store_image_url")
        val imageUrl: String,
        @SerializedName("store_flag")
        val flagImageUrl: String,
        @SerializedName("last_update")
        val lastUpdate: String? = null,
        @SerializedName("valid")
        val valid: Int = 0,
        @SerializedName("shop_country")
        val countryCode: String? = ""
) : Serializable