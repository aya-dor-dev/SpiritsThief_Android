package com.spiritsthief.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Dor Ayalon on 1/9/18.
 */
class ApiClient{
    companion object {
        private const val BASE_URL = "https://thespiritthief.com"
        private const val PORT = "443"

        fun get(): IApi {
            return DemoApi()

//            val okHttpClient = OkHttpClient.Builder()
//                    .addNetworkInterceptor {
//                        val requestBuilder = it.request().newBuilder()
//                        requestBuilder.header("Content-Type", "application/json")
//                        it.proceed(requestBuilder.build())
//                    }.readTimeout(60, TimeUnit.SECONDS)
//                    .build()
//
//            val retrofit = Retrofit.Builder()
//                    .addConverterFactory(
//                            GsonConverterFactory.create())
//                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//                    .baseUrl("$BASE_URL:$PORT/")
//                    .client(okHttpClient)
//                    .build()
//
//            return retrofit.create(IApi::class.java)
        }

        fun getBarcodeApi(): IBarcodeApi {
            val okHttpClient = OkHttpClient.Builder()
                    .addNetworkInterceptor {
                        val requestBuilder = it.request().newBuilder()
                        requestBuilder.header("Content-Type", "application/json")
                        it.proceed(requestBuilder.build())
                    }.readTimeout(60, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl("https://api.upcitemdb.com/prod/trial/")
                    .client(okHttpClient)
                    .build()

            return retrofit.create(IBarcodeApi::class.java)
        }
    }

}