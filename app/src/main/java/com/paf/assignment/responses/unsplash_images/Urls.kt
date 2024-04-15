package com.paf.assignment.responses.unsplash_images


import com.google.gson.annotations.SerializedName

import com.google.gson.annotations.Expose


data class Urls(
    @SerializedName("full")
    @Expose
    val full: String?,
    @SerializedName("raw")
    @Expose
    val raw: String?,
    @SerializedName("regular")
    @Expose
    val regular: String?,
    @SerializedName("small")
    @Expose
    val small: String?,
    @SerializedName("small_s3")
    @Expose
    val smallS3: String?,
    @SerializedName("thumb")
    @Expose
    val thumb: String?
)