package com.paf.assignment.utils

import android.graphics.Bitmap
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun Throwable.getErrorMessage(): String {
    return when (this) {
        is SocketTimeoutException -> "Slow Internet Connection"
        is UnknownHostException -> "Server not responding/No internet connection"
        else -> "Something went Wrong"
    }
}

fun Bitmap.resizeImage(): Bitmap {

    val width = this.width
    val height = this.height

    val scaleWidth = width * 2/3
    val scaleHeight = height * 2/3

    if (this.byteCount <= 1000000)
        return this

    return Bitmap.createScaledBitmap(this, scaleWidth, scaleHeight, false)
}

