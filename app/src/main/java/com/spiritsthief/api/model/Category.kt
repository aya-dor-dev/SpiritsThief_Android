package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dorayalon on 01/02/2018.
 */
data class Category(
        @SerializedName("category")
        val name: String,
        @SerializedName("subcategory")
        val subcategories: List<String>?,
        val iconResId: Int
)