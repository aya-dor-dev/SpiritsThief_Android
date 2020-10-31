package com.spiritsthief.api.model

import com.google.gson.annotations.SerializedName
import com.spiritsthief.api.ApiRequest
import com.spiritsthief.common.UserPreferences

/**
 * Created by dorayalon on 04/02/2018.
 */
class ShopsRequest(@SerializedName("product_id")
                   val productId: Long): ApiRequest()