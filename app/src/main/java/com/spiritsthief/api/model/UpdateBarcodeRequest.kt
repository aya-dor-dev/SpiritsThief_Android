package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName

data class UpdateBarcodeRequest(@SerializedName("ID") val id: String,
                                @SerializedName("barcode") val barcode: String)