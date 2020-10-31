package com.spiritsthief.api

import com.google.gson.annotations.SerializedName
import com.spiritsthief.api.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Dor Ayalon on 1/9/18.
 */
interface IApi {
    @GET("autocomplete")
    fun getAutocompleteOptions(@Query("name") ids: String): Deferred<List<AutoCompleteOption>>

    @POST("bottlers/")
    fun getBottlers(@Body req: ApiRequest): Deferred<Response<Bottler>>

    @POST("bottles/")
    fun getBottles(@Body sr: SearchRequest): Call<Response<Bottle>>

    @POST("bottles/")
    fun getBottles2(@Body sr: SearchRequest): Deferred<Response<Bottle>>

    @GET("GetBottlesByID")
    fun getBottlesById(@Query("id") ids: String): Call<List<Bottle>>

    @POST("casks/")
    fun getCaskTypes(): Deferred<Response<CaskType>>

    @POST("distilleries/")
    fun getDistilleries(@Body req: ApiRequest): Deferred<Response<Distillery>>

    @POST("shops/")
    fun getShopsForBottle(@Body req: ShopsRequest): Deferred<List<Store>>

    @POST("countries/")
    fun getCountries(@Body req: ApiRequest): Deferred<Response<Country>>

    @POST("regions/")
    fun getRegions(@Body req: ApiRequest): Deferred<Response<Region>>

    @POST("types/")
    fun getCategories(@Body req: ApiRequest): Deferred<Response<Category>>

    @POST("stats/")
    fun getStats(): Call<List<Stats>>

    @POST("shop_stats/")
    fun getShopStats(@Body req: ApiRequest): Call<List<Stats>>

    @POST("deals/")
    fun getDeals(@Body req: ApiRequest): Call<Response<Deal>>

    @POST("invalid_img/")
    fun reportInvalidImage(@Body req: InvalidImage): Call<Void>

    @POST("add_barcode/")
    fun updateBarcode(@Body req: UpdateBarcodeRequest): Deferred<retrofit2.Response<Void>>
}
