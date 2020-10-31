package com.spiritsthief.ui.adapter

import android.graphics.drawable.Drawable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.InvalidImage
import com.spiritsthief.common.GlideApp
import java.lang.Exception


/**
 * Created by Dor Ayalon on 12/18/17.
 */
abstract class BaseBottlesListAdapter<T: BottleHolder>(private val whiskiesList: MutableList<Bottle>,
                                                       val onBottleClick: (Bottle, CardView) -> Unit,
                                                       private val pagedListHandler: PagedListHandler?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VT_BOTTLE = 0
    private val VT_LOADING = 1

    init {
        if (pagedListHandler?.hasMore() == true) {
            whiskiesList.add(LoadingBottles())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VT_BOTTLE -> onCreateBottleViewHolder(parent)
        else -> Loading(LayoutInflater.from(parent.context).inflate(getLoadingViewResourceId(), parent, false))
    }

    abstract fun onCreateBottleViewHolder(parent: ViewGroup): T

    @LayoutRes
    open fun getLoadingViewResourceId(): Int = R.layout.loading_list_item

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? BottleHolder)?.let {
            it.bind(whiskiesList[position])
        }

        if (pagedListHandler != null && position == whiskiesList.size - 5 && pagedListHandler.hasMore()) {
            pagedListHandler.loadMode()
        }
    }

    override fun getItemViewType(position: Int) = if (whiskiesList[position] is LoadingBottles) VT_LOADING else VT_BOTTLE

    override fun getItemCount(): Int {
        return whiskiesList.size
    }

    fun setItems(list: List<Bottle>) {
        val startIndex = whiskiesList.size
        whiskiesList.clear()
        whiskiesList.addAll(list)

        if (pagedListHandler?.hasMore() == true) {
            whiskiesList.add(LoadingBottles())
            notifyItemRangeInserted(startIndex, whiskiesList.size)
        } else {
            notifyItemRangeInserted(startIndex, whiskiesList.size - 1)
        }
    }
}

abstract class BottleHolder(itemView: View, private val onBottleClick: (Bottle, CardView) -> Unit) : RecyclerView.ViewHolder(itemView) {
    protected lateinit var bottle: Bottle

    fun bind(bottle: Bottle) {
        this.bottle = bottle
        bind()
    }

    abstract fun bind()
}

class Loading(itemView: View): RecyclerView.ViewHolder(itemView)

class LoadingBottles: Bottle()