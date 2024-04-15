package com.paf.assignment.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.paf.assignment.R
import com.paf.assignment.adapter.ImagesAdapter
import com.paf.assignment.utils.PagingResponseStates
import com.paf.assignment.utils.PagingState
import com.paf.assignment.utils.getErrorMessage
import com.paf.assignment.view_models.UnsplashViewModel
import kotlinx.android.synthetic.main.activity_main.btn_retry
import kotlinx.android.synthetic.main.activity_main.main_progress_bar
import kotlinx.android.synthetic.main.activity_main.rv_image_list
import kotlinx.android.synthetic.main.activity_main.tv_error

class MainActivity : AppCompatActivity() {

    private val imagesAdapter: ImagesAdapter by lazy {
        ImagesAdapter(this)
    }

    private val unsplashViewModel: UnsplashViewModel by lazy {
        ViewModelProvider(this)[UnsplashViewModel::class.java]
    }

    private val pagingStates: MutableLiveData<PagingResponseStates> by lazy {
        MutableLiveData<PagingResponseStates>().also {
            it.observe(this) { pagingResponseStates ->
                main_progress_bar.visibility =
                    if (imagesAdapter.currentList.isNullOrEmpty() && pagingResponseStates.state == PagingState.LOADING
                    ) View.VISIBLE else View.GONE
                if (!imagesAdapter.currentList.isNullOrEmpty()) {
                    imagesAdapter.setState(pagingResponseStates.state)
                }

                if (PagingState.DONE == pagingResponseStates.state) {
                    tv_error.visibility = View.GONE
                    btn_retry.visibility = View.GONE
                    rv_image_list.visibility = View.VISIBLE
                }

                if (pagingResponseStates.state == PagingState.ERROR && imagesAdapter.currentList.isNullOrEmpty()) {
                    tv_error.visibility = View.VISIBLE
                    btn_retry.visibility = View.VISIBLE
                    rv_image_list.visibility = View.GONE
                    tv_error.text =
                        pagingResponseStates.throwable?.getErrorMessage() ?: "Something went wrong"
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_image_list.adapter = imagesAdapter
        btn_retry.setOnClickListener {
            setPagination()
        }
        setPagination()
    }

    private fun setPagination() {
        unsplashViewModel
            .createImagesDataSource(pagingStates)
            .observe(this) {
                imagesAdapter.submitList(it)
            }
    }
}