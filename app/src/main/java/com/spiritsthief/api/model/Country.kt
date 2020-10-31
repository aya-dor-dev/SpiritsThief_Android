package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Dor Ayalon on 1/16/18.
 */
data class Country(
        @SerializedName("country")
        val name: String,
        @SerializedName("count")
        val count: Int
)