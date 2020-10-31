package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName
import com.spiritsthief.api.ApiRequest
import com.spiritsthief.common.CURRENCY
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.common.sameContent
import java.io.Serializable

/**
 * Created by Dor Ayalon on 1/3/18.
 */
class SearchRequest : ApiRequest(), Serializable {
    companion object {
        const val SORT_BY_NAME = "name"
        const val SORT_BY_AVG_PRICE = "price"
        const val SORT_BY_AGE = "age"
        const val SORT_BY_VINTAGE = "vintage"
        const val DEFAULT_VAL = -1

        fun getYear(): Int {
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            return cal.get(java.util.Calendar.YEAR)
        }
    }

    @SerializedName("ID")
    var id = mutableListOf<Long>()

    @SerializedName("barcode")
    var barcode = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("bottler")
    var bottler = mutableListOf<String>()

    @SerializedName("ex_liquid")
    var exLiquid: List<String> = listOf()

    @SerializedName("casksize")
    var caskSize: List<String> = listOf()

    @SerializedName("wood")
    var wood: List<String> = listOf()

    @SerializedName("refill")
    var refill: List<String> = listOf()

    @SerializedName("minPrice")
    var minPrice = DEFAULT_VAL

    @SerializedName("maxPrice")
    var maxPrice = DEFAULT_VAL

    @SerializedName("minABV")
    var minABV = DEFAULT_VAL

    @SerializedName("maxABV")
    var maxABV = DEFAULT_VAL

    @SerializedName("minAge")
    var minAge = DEFAULT_VAL

    @SerializedName("maxAge")
    var maxAge = DEFAULT_VAL

    @SerializedName("includingNas")
    var includingNas = true

    @SerializedName("onlyCaskStrength")
    var onlyCaskStrength = false

    @SerializedName("onlySingleCask")
    var onlySingleCask = false

    @SerializedName("minDistillationYear")
    var minDistillationYear = DEFAULT_VAL

    @SerializedName("maxDistillationYear")
    var maxDistillationYear = DEFAULT_VAL

    @SerializedName("minBottlingYear")
    var minBottlingYear = DEFAULT_VAL

    @SerializedName("maxBottlingYear")
    var maxBottlingYear = DEFAULT_VAL

    @SerializedName("limit")
    val limit = 20

    @SerializedName("offset")
    var offset = 0

    @SerializedName("count_needed")
    var calculateCount = false

    @SerializedName("valid")
    var valid = DEFAULT_VAL

    @SerializedName("last_update")
    var lastUpdate = DEFAULT_VAL

    fun onlyVerified() = valid == 1

    fun onlyVerified(b: Boolean) {
        lastUpdate = if (b) 7 else DEFAULT_VAL
        valid = if (b) 1 else DEFAULT_VAL
    }

    override fun equals(other: Any?) = other is SearchRequest &&
            super.equals(other) &&
            id.sameContent(other.id) &&
            barcode == other.barcode &&
            name == other.name &&
            bottler == other.bottler &&
            exLiquid == other.exLiquid &&
            caskSize == other.caskSize &&
            wood == other.wood &&
            refill == other.refill &&
            minPrice == other.minPrice &&
            maxPrice == other.maxPrice &&
            minABV == other.minABV &&
            maxABV == other.maxABV &&
            minAge == other.minAge &&
            maxAge == other.maxAge &&
            includingNas == other.includingNas &&
            onlyCaskStrength == other.onlyCaskStrength &&
            onlySingleCask == other.onlySingleCask &&
            minDistillationYear == other.minDistillationYear &&
            maxDistillationYear == other.maxDistillationYear &&
            minBottlingYear == other.minBottlingYear &&
            maxBottlingYear == other.maxBottlingYear &&
            onlyVerified() == other.onlyVerified()

    fun hasData() = this.barcode.isNotEmpty() ||
            this.name.isNotEmpty() ||
            this.brand.isNotEmpty() ||
            this.bottler.isNotEmpty() ||
            this.exLiquid.isNotEmpty() ||
            this.refill.isNotEmpty() ||
            this.caskSize.isNotEmpty() ||
            this.wood.isNotEmpty() ||
            this.region.isNotEmpty() ||
            this.country.isNotEmpty() ||
            this.category.isNotEmpty() ||
            this.onlyVerified()

    fun copy() = SearchRequest().apply {
        val original = this@SearchRequest
        category = original.category
        subcategory = original.subcategory
        region = original.region
        country = original.country
        brand = original.brand
        sortBy = original.sortBy
        sortOrder = original.sortOrder
        currency = original.currency
        deliveryCountry = original.deliveryCountry
        id = original.id
        barcode = original.barcode
        name = original.name
        bottler = original.bottler
        exLiquid = original.exLiquid
        caskSize = original.caskSize
        wood = original.wood
        refill = original.refill
        minPrice = original.minPrice
        maxPrice = original.maxPrice
        minABV = original.minABV
        maxABV = original.maxABV
        minAge = original.minAge
        maxAge = original.maxAge
        includingNas = original.includingNas
        onlyCaskStrength = original.onlyCaskStrength
        onlySingleCask = original.onlySingleCask
        minDistillationYear = original.minDistillationYear
        maxDistillationYear = original.maxDistillationYear
        minBottlingYear = original.minBottlingYear
        maxBottlingYear = original.maxBottlingYear
        offset = original.offset
        calculateCount = original.calculateCount
        valid = original.valid
        lastUpdate = original.lastUpdate
    }

    fun getCountOfFilters(): Int {
        var count = 0
        if (brand.isNotEmpty()) count++
        if (bottler.isNotEmpty()) count++
        if (exLiquid.isNotEmpty()) count++
        if (wood.isNotEmpty()) count++
        if (refill.isNotEmpty()) count++
        if (caskSize.isNotEmpty()) count++
        if (region.isNotEmpty()) count++
        if (country.isNotEmpty()) count++
        if (minPrice != DEFAULT_VAL || maxPrice != DEFAULT_VAL) count++
        if (minABV != DEFAULT_VAL || maxABV != DEFAULT_VAL) count++
        if (minAge != DEFAULT_VAL || maxAge != DEFAULT_VAL) count++
        if (minDistillationYear != DEFAULT_VAL || maxDistillationYear != DEFAULT_VAL) count++
        if (minBottlingYear != DEFAULT_VAL || maxBottlingYear != DEFAULT_VAL) count++
        if (onlyCaskStrength) count++
        if (onlySingleCask) count++
        if (onlyVerified()) count++

        return count
    }

    fun increaseOffset() {
        offset += limit
    }

    fun setCaskType(ct: CaskType) {
        this.exLiquid = ct.exLiquid
        this.refill = ct.refill
        this.caskSize = ct.caskSize
        this.wood = ct.wood
    }

    fun isBarcodeSearch() = barcode.isNotEmpty()
}