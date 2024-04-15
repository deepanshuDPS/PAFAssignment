package com.paf.assignment.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.paf.assignment.network.paging_data_sources.data_source_factory.UnsplashDataSourceFactory
import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponseItem
import com.paf.assignment.utils.PagingResponseStates
import io.reactivex.disposables.CompositeDisposable

class UnsplashViewModel : ViewModel() {


    private val disposables = ArrayList<CompositeDisposable>()


    fun createImagesDataSource(
        pagingState: MutableLiveData<PagingResponseStates>
    ): LiveData<PagedList<UnsplashImagesResponseItem>> {

        val dataSourceFactory = UnsplashDataSourceFactory(pagingState)
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setInitialLoadSizeHint(30)
            .setEnablePlaceholders(false)
            .build()
        dataSourceFactory.compositeDisposable?.let {
            disposables.add(it)
        }
        return LivePagedListBuilder(dataSourceFactory, config).build()
    }


    override fun onCleared() {
        super.onCleared()
        disposables.forEach {
            it.clear()
        }
    }
}