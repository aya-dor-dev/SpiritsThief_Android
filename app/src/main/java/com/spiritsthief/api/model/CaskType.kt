package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Dor Ayalon on 1/11/18.
 */
data class CaskType(
        @SerializedName("ex_liquid")
        var exLiquid: List<String>,
        @SerializedName("casksize")
        var caskSize: List<String>,
        @SerializedName("wood")
        var wood: List<String>,
        @SerializedName("refill")
        var refill: List<String>
) {
    fun isNotEmpty() =
            exLiquid.isNotEmpty() ||
                    caskSize.isNotEmpty() ||
                    wood.isNotEmpty() ||
                    refill.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        return if (other !is CaskType) {
            false
        } else {
            exLiquid == other.exLiquid &&
                    caskSize == other.caskSize &&
                    wood == other.wood &&
                    refill == other.refill
        }
    }
}