package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dorayalon on 27/01/2018.
 */
class Response<T>(
        @SerializedName("meta")
        val meta: Meta,
        @SerializedName("results")
        val results: MutableList<T>
){

    public data class Meta(
            val count: Int
    )
}