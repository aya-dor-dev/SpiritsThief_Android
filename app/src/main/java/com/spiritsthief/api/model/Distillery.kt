package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Dor Ayalon on 1/11/18.
 */
data class Distillery(
        @SerializedName("brand")
        val name: String,
        @SerializedName("image_url")
        val imageUrl: String = "http://reddishpink.media/wp-content/uploads/2015/03/cadenheads.png",
        @SerializedName("country")
        val country: String,
        @SerializedName("region")
        val region: String = "http://reddishpink.media/wp-content/uploads/2015/03/cadenheads.png"
)
