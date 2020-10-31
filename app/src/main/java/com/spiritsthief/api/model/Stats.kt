package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dorayalon on 04/02/2018.
 */
data class Stats(
        @SerializedName("num_distilleries")
        val brands: Int,
        @SerializedName("num_links")
        val links: Int,
        @SerializedName("num_bottler")
        val bottlers: Int,
        @SerializedName("num_products")
        val products: Int,
        @SerializedName("num_stores")
        val stores: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("store_flag")
        val storeFlag: String,
        @SerializedName("country")
        val countryCode: String
)