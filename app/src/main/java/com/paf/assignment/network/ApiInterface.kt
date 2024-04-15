package com.paf.assignment.network

import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {

    @GET("photos")
    fun getUnsplashImage(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("client_id") clientId: String = ApiConstants.CLIENT_ID
    ): Single<UnsplashImagesResponse>


    @GET
    fun loadImage(@Url url: String): Single<ResponseBody>
}