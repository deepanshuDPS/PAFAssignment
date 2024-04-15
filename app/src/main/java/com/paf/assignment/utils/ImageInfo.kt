package com.paf.assignment.utils

data class ImageInfo(
    val url: String,
    var id:String,
    var retry:Int=0,
    var status: ImageLoadStatus = ImageLoadStatus.LOADING,
    var path: String? = null
)

enum class ImageLoadStatus {
    LOADING,
    CACHED,
    ERROR
}
