package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Dor Ayalon on 1/9/18.
 */
data class Bottler (
        @SerializedName("bottler")
        val name: String,
        @SerializedName("image_url")
        val imageUrl: String?,
        @SerializedName("country")
        val country: String,
        @SerializedName("region")
        val region: String = "http://reddishpink.media/wp-content/uploads/2015/03/cadenheads.png"
)