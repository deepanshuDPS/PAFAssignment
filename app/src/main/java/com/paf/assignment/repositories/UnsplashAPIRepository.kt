package com.paf.assignment.repositories


import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.paf.assignment.network.ApiInterface
import com.paf.assignment.network.ApiResponse
import com.paf.assignment.network.api_controllers.PAFApiController
import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
object UnsplashAPIRepository {

    val apiClient = PAFApiController()
        .getClient()?.create(ApiInterface::class.java)


    fun getUnsplashImages(pageSize: Int, pageIndex: Int): MutableLiveData<ApiResponse> {
        return MutableLiveData<ApiResponse>().also { liveData ->
            apiClient?.getUnsplashImage(
                pageIndex,
                pageSize
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    {
                        liveData.setResult(it)
                    },
                    {
                        liveData.setResult(error = it)
                    }
                )
        }
    }


    private fun MutableLiveData<ApiResponse>.setResult(
        success: UnsplashImagesResponse? = null,
        error: Throwable? = null
    ) {
        success?.let {
            value = ApiResponse(response = it)
        }
        error?.let {
            value = ApiResponse(error = it)
        }
    }

}