package com.paf.assignment.network.paging_data_sources

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.paf.assignment.utils.PagingResponseStates
import com.paf.assignment.utils.PagingState
import com.paf.assignment.repositories.UnsplashAPIRepository
import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponseItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UnsplashDataSource(
    private val state: MutableLiveData<PagingResponseStates>
) :
    PageKeyedDataSource<Int, UnsplashImagesResponseItem>() {

    val compositeDisposable = CompositeDisposable()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, UnsplashImagesResponseItem>
    ) {
        updateStates(PagingState.LOADING)
        // AppApiRepository.appApiClient
        UnsplashAPIRepository.apiClient
            ?.getUnsplashImage(1, 30)
            ?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { success, error ->
                success?.let {
                    updateStates(PagingState.DONE)
                    val nextPage = 2
                    if (!it.isEmpty()) {
                        callback.onResult(it.toList(), null, nextPage)
                    }
                }

                error?.let {
                    updateStates(PagingState.ERROR, it)
                }
            }?.also {
                compositeDisposable.add(it)
            }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UnsplashImagesResponseItem>
    ) {
        //   AppApiRepository.appApiClient
        updateStates(PagingState.LOADING)

        UnsplashAPIRepository.apiClient
            ?.getUnsplashImage(params.key, 30)
            ?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { success, error ->
                success?.let {
                    updateStates(PagingState.DONE)
                    val nextPage = params.key + 1
                    if (!it.isEmpty()) {
                        callback.onResult(it, nextPage)
                    }
                }

                error?.let {
                    updateStates(PagingState.ERROR, it)
                }
            }?.also {
                compositeDisposable.add(it)
            }
    }


    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UnsplashImagesResponseItem>
    ) {

    }

    private fun updateStates(pagingState: PagingState, throwable: Throwable? = null) {
        state.postValue(PagingResponseStates(pagingState, throwable))
    }
}