package com.paf.assignment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paf.assignment.R
import com.paf.assignment.utils.PagingState
import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponseItem
import com.paf.assignment.utils.ImageInfo
import com.paf.assignment.utils.ImageLoader
import kotlinx.android.synthetic.main.item_image.view.iv_unsplash_image
import kotlinx.android.synthetic.main.load_more_view.view.loadmore_progress

class ImagesAdapter(
    private val mContext: Context
) : PagedListAdapter<UnsplashImagesResponseItem, RecyclerView.ViewHolder>(diffCallback) {

    private var state = PagingState.DONE

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && (state == PagingState.LOADING)
    }


    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) ITEM else LOADING
    }

    fun setState(state: PagingState) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        return when (viewType) {
            ITEM -> {
                val view: View =
                    inflater.inflate(R.layout.item_image, parent, false)
                ListViewHolder(view)
            }

            else -> {
                val v2: View = inflater.inflate(R.layout.load_more_view, parent, false)
                ProgressViewHolder(v2)
            }
        }
    }

    inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (getItemViewType(position)) {
            LOADING -> (viewHolder as ProgressViewHolder).itemView.loadmore_progress.visibility =
                View.VISIBLE

            ITEM -> {
                (viewHolder as ListViewHolder).itemView.apply {
                    val singleItem: UnsplashImagesResponseItem? = getItem(position)
                    Log.d("url_data", singleItem?.urls?.small ?: "N/A")
                    Log.d("img_data", this.iv_unsplash_image.toString())
                    singleItem?.urls?.small?.let {
                        val url = /*if (position % 10 == 0) "https://nothing.png" else*/ it
                        ImageInfo(url, position.toString()).also { info ->
                            ImageLoader.loadImage(mContext, info, this.iv_unsplash_image)
                        }
                    }
                }

            }
        }
    }

    inner class ListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    companion object {
        const val ITEM = 0
        const val LOADING = 1

        val diffCallback = object : DiffUtil.ItemCallback<UnsplashImagesResponseItem>() {
            override fun areItemsTheSame(
                oldItem: UnsplashImagesResponseItem,
                newItem: UnsplashImagesResponseItem
            ): Boolean {
                return oldItem.urls == newItem.urls
            }

            override fun areContentsTheSame(
                oldItem: UnsplashImagesResponseItem,
                newItem: UnsplashImagesResponseItem
            ): Boolean {
                return oldItem.urls == newItem.urls
            }
        }
    }
}