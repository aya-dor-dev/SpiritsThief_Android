package com.spiritsthief.api

import android.text.TextUtils
import androidx.annotation.CallSuper
import com.google.gson.annotations.SerializedName
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.common.sameContent
import java.io.Serializable

/**
 * Created by dorayalon on 01/02/2018.
 */
open class ApiRequest : Serializable {
    companion object {
        const val SORT_ASC = "ASC"
        const val SORT_DESC = "DESC"
    }

    @SerializedName("category")
    var category = mutableListOf("Whisky")

    @SerializedName("subcategory")
    var subcategory = mutableListOf<String>()

    @SerializedName("region")
    var region = mutableListOf<String>()

    @SerializedName("country")
    var country = mutableListOf<String>()

    @SerializedName("brand")
    var brand = mutableListOf<String>()

    @SerializedName("sort")
    var sortBy: String = "RANDOM"

    @SerializedName("sort_order")
    var sortOrder: String = SORT_ASC

    @SerializedName("currency")
    var currency: String = UserPreferences.currency.value!!.name

    @SerializedName("delivery_country")
    var deliveryCountry: List<String> = when (UserPreferences.country.value) {
        "" -> listOf()
        else -> listOf(UserPreferences.country.value!!)
    }

    fun setSort(sort: String, order: String) {
        this.sortBy = sort
        this.sortOrder = order
    }

    override fun equals(other: Any?) = if (other is ApiRequest) {
        category.sameContent(other.category) &&
                subcategory.sameContent(other.subcategory) &&
                region.sameContent(other.region) &&
                country.sameContent(other.country) &&
                brand.sameContent(other.brand) &&
                sortBy == other.sortBy &&
                sortOrder == other.sortOrder &&
                currency == other.currency &&
                deliveryCountry.sameContent(other.deliveryCountry)
    } else false
}