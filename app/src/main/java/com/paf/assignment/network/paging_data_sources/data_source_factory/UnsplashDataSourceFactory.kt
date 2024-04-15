package com.paf.assignment.network.paging_data_sources.data_source_factory

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.paf.assignment.network.paging_data_sources.UnsplashDataSource
import com.paf.assignment.utils.PagingResponseStates
import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponseItem
import io.reactivex.disposables.CompositeDisposable

class UnsplashDataSourceFactory(
    private val state: MutableLiveData<PagingResponseStates>
) :
    DataSource.Factory<Int, UnsplashImagesResponseItem>() {

    private val newsDataSourceLiveData = MutableLiveData<UnsplashDataSource>()
    var compositeDisposable: CompositeDisposable? = null

    override fun create(): DataSource<Int, UnsplashImagesResponseItem> {
        val newsDataSource = UnsplashDataSource(state)
        newsDataSourceLiveData.postValue(newsDataSource)
        compositeDisposable = newsDataSource.compositeDisposable
        return newsDataSource
    }


}