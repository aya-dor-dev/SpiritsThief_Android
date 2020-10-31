package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Dor Ayalon on 1/16/18.
 */
data class Region(
        @SerializedName("country")
        val country: String,
        @SerializedName("region")
        var name: String,
        @SerializedName("count")
        var count: Int)

