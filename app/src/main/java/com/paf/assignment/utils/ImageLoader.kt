package com.paf.assignment.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.paf.assignment.R
import com.paf.assignment.repositories.UnsplashAPIRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit

object ImageLoader {

    private val imageInfoMap = mutableMapOf<String, ImageInfo>()
    private val visibleIvMap = mutableMapOf<ImageView, String>()

    private fun loadFromNetwork(imageInfo: ImageInfo, loadInto: ImageView): String? {
        loadInto.let {
            it.setImage(imageInfo.id, resource = R.drawable.image_placeholder)
            UnsplashAPIRepository.apiClient?.loadImage(imageInfo.url)
                ?.blockingGet()?.let { rBody ->
                    val filePath =
                        saveToCache(it.context, imageInfo.url, rBody.byteStream())?.absolutePath
                    imageInfo.status = ImageLoadStatus.CACHED
                    return filePath
                }
        }
        return null
    }

    private fun isImageCached(context: Context, url: String): String? {
        context.let {
            val filePath = getFilePath(context, url)
            try {
                if (filePath.exists()) {
                    return filePath.absolutePath
                }
            } catch (e: Exception) {
                // if any error in setting image or not found
                return null
            }
        }
        return null
    }

    @Synchronized
    fun ImageView.setImage(id: String, resource: Int? = null, filePath: String? = null) {
        if (visibleIvMap[this] == id) {
            resource?.let {
                this.setImageResource(it)
            }
            filePath?.let {
                val bitmap = BitmapFactory.decodeFile(filePath)
                this.setImageBitmap(bitmap.resizeImage())
            }
        }
    }

    fun loadImage(
        context: Context,
        mImageInfo: ImageInfo,
        loadInto: ImageView,
        isRetrying: Boolean = false
    ) {

        // whenever load image calls, newId is assigned to imageview
        if (!isRetrying)
            visibleIvMap[loadInto] = mImageInfo.id
        //        Log.d("visible_image_map", visibleIvMap.toString())
        fun someError() {
            // only 3 retry
            loadInto.setImage(mImageInfo.id, resource = R.drawable.image_placeholder_error)
            if (mImageInfo.retry < 3) {
                mImageInfo.retry++
                val value = Observable.interval(2, TimeUnit.SECONDS)
                Log.d("delay", value.toString())
                // load image again
                loadImage(context, mImageInfo, loadInto, true)
            }
        }

        Observable.create<Any> { emitter ->
            // checking is image in queue or not
            val isImageInfo = imageInfoMap[mImageInfo.url]
            if (isImageInfo != null) {
                isImageCached(context, isImageInfo.url).let { imgCached ->
                    if (imgCached == null) {
                        when (isImageInfo.status) {
                            ImageLoadStatus.ERROR, ImageLoadStatus.LOADING -> {
                                // in queue or retrying
                            }

                            else -> {
                                // load from network
                                loadFromNetwork(isImageInfo, loadInto)?.let {
                                    // send send path on complete
                                    emitter.onNext(it)
                                    emitter.onComplete()
                                    return@create
                                }
                                // some error false
                                emitter.onNext(false)
                            }
                        }
                    } else {
                        // if in queue is it cached then send success for it
                        emitter.onNext(imgCached)
                    }
                }
            } else {
                isImageCached(context, mImageInfo.url).let { imgCached ->
                    if (imgCached != null) {
                        // image cached use it
                        emitter.onNext(imgCached)
                    } else {
                        imageInfoMap[mImageInfo.id] = mImageInfo
                        // new image info
                        // load from network
                        loadFromNetwork(mImageInfo, loadInto)?.let {
                            // send send path on complete
                            emitter.onNext(it)
                            emitter.onComplete()
                            return@create
                        }
                        // some error false
                        emitter.onNext(false)
                    }
                }
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it is String) {
                    // set image if loaded to it's image view
                    loadInto.setImage(mImageInfo.id, filePath = it)
                } else {
                    // set error resource and retry
                    someError()
                }

            }, {
                // set error resource and retry
                someError()
            }).also {
                // when free will dispose automatically
                Log.d("disposable", it.toString())
            }


    }

    private fun saveToCache(context: Context, url: String, inputStream: InputStream?): File? {
        try {
            val fileDir = File(context.cacheDir.absolutePath, "unsplashfiles")
            if (!fileDir.exists())
                fileDir.mkdir()
            inputStream?.use { input ->
                val file = File(fileDir, getFileNameFromUrl(url))
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
                return file
            }
        } catch (e: IOException) {
            Log.e("ImageLoader", "Error saving image to cache: ${e.message}")
        }
        return null
    }

    private fun getFilePath(context: Context, url: String) =
        File(context.cacheDir?.absolutePath + "/unsplashfiles", getFileNameFromUrl(url))

    private fun getFileNameFromUrl(url: String): String {
        return url.substringAfterLast("/") // .substringBeforeLast("?")
    }
}
