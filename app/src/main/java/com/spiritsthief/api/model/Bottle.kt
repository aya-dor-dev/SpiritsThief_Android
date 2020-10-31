package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Dor Ayalon on 12/18/17.
 */
open class Bottle : Serializable {
    @SerializedName("id")
    val id: Long = -1
    @SerializedName("region")
    val region: String? = null
    @SerializedName("style")
    val style: String? = null
    @SerializedName("image_urls")
    var imageUrl: ArrayList<String> = arrayListOf()
    @SerializedName("brand")
    val distillery: String? = null
    @SerializedName("bottlingyear")
    val bottlingDate: Long? = null
    @SerializedName("bottler")
    val bottler: String? = null
    @SerializedName("series")
    val series: String? = null
    @SerializedName("name")
    val name: String = ""
    @SerializedName("colouring")
    val colouring: String? = null
    @SerializedName("size")
    val size: String? = null
    @SerializedName("barcode")
    val barcode: String? = null
    @SerializedName("casktype")
    val caskType: String? = null
    @SerializedName("ex_liquid")
    val exLiquid: List<String>? = null
    @SerializedName("casksize")
    val caskSize: List<String>? = null
    @SerializedName("wood")
    val caskWood: List<String>? = null
    @SerializedName("refill")
    val refill: List<String>? = null
    @SerializedName("casknumber")
    val caskNumber: String? = null
    @SerializedName("country")
    val country: String? = null
    @SerializedName("vintage")
    val vintage: String? = null
    @SerializedName("age")
    val age: String? = null
    @SerializedName("nobottles")
    val numberOfBottles: String? = null
    @SerializedName("strength")
    val abv: String? = null
    @SerializedName("price")
    val avgPrice: String? = null
    @SerializedName("currency")
    val currency: String? = null
    val category: String? = null
}