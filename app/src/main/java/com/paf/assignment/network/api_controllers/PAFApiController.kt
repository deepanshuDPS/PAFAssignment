package com.paf.assignment.network.api_controllers

import com.paf.assignment.network.ApiConstants
import com.paf.assignment.network.interceptors.PAFApiInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PAFApiController {

    private var retrofit: Retrofit? = null
    private val okHttpClientBuilder = OkHttpClient.Builder()

    fun getClient(): Retrofit? {
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(PAFApiInterceptor())
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(ApiConstants.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofit
    }

}