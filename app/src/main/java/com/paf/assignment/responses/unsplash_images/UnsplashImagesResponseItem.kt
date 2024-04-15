package com.paf.assignment.responses.unsplash_images


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class UnsplashImagesResponseItem(
    @SerializedName("urls")
    @Expose
    val urls: Urls?
)