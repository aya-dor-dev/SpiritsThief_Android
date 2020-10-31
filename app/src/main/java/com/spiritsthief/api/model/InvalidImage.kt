package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

data class InvalidImage(@SerializedName("ID") val bottleId: String,
                        @SerializedName("url") val imageUrl: String)


