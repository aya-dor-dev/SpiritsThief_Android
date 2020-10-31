package com.spiritsthief.ui.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.common.*
import com.spiritsthief.ui.adapter.BaseBottlesListAdapter
import com.spiritsthief.ui.adapter.PagedListHandler
import com.spiritsthief.ui.ui.main.BaseBottlesViewModel
import com.spiritsthief.ui.ui.main.BaseFragment
import com.spiritsthief.ui.ui.main.STATUS
import com.spiritsthief.ui.viewbottle.ViewBottleActivity
import kotlinx.android.synthetic.main.bottles_list_container.*
import java.io.ByteArrayOutputStream

abstract class BottlesListFragment<T: BaseBottlesListAdapter<*>>: BaseFragment() {
    var viewModel: BaseBottlesViewModel? = null
    var adapter: T? = null

    private val searchStatusObserver: Observer<STATUS> = Observer {
        when (it) {
            STATUS.IN_PROGRESS -> {
                clear()
                loadingBottles()
            }
            STATUS.ERROR -> networkError()
            STATUS.FINISHED -> bottlesLoaded()
        }
    }

    fun registerViewModel(viewModel: BaseBottlesViewModel) {
        this.viewModel = viewModel
        this.viewModel?.status?.observe(this, searchStatusObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.status?.removeObserver(searchStatusObserver)
    }

    open fun loadingBottles() {
        error_container.hide()
        loading.rotateForever()
        loading_container.show()
    }

    open fun networkError() {
        error_message.text = getString(R.string.server_error_message)
        loading_container.hide()
        error_container.show()
    }

    open fun bottlesLoaded() {
        if (viewModel!!.count!! > 0) {
            loading.stopRotating()
            if (adapter == null) {
                bottles_list.layoutManager = getLayoutManager()
                bottles_list.layoutAnimation = AnimationUtils.loadLayoutAnimation(activity!!, R.anim.layout_animation_falldown)
                val list = mutableListOf<Bottle>()
                list.addAll(viewModel!!.bottles!!)
                adapter = getAdapter(list, onItemClick, viewModel)
                bottles_list.adapter = adapter
            } else {
                adapter?.setItems(viewModel!!.bottles!!)
            }
        } else {
            displayNoResultsError()
        }
        loading_container.hide()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.apply {
            when (status.value) {
                STATUS.IN_PROGRESS -> loadingBottles()
                STATUS.ERROR -> networkError()
                STATUS.FINISHED -> {
                    error_container.hide()
                    loading_container.hide()
                    bottles_list.show()
                }
            }
        }
    }

    abstract fun getAdapter(list: MutableList<Bottle>,
                        onBottleClick: (Bottle, CardView) -> Unit,
                        pagedListHandler: PagedListHandler?): T

    open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    fun displayNoResultsError() {
        error_message.text = getString(getNoResultsErrorMessage())
        loading_container.hide()
        error_container.show()
    }

    open fun clear() {
        hideSoftKeybard()

        adapter?.apply {
            if (itemCount > 0) bottles_list.scrollToPosition(0)
        }

        adapter = null
        bottles_list.adapter = null
        viewModel?.clear()
    }

    @StringRes
    abstract fun getNoResultsErrorMessage(): Int

    @DrawableRes
    abstract fun getNoResultsErrorIcon(): Int

    private var onItemClick: (Bottle, CardView) -> Unit = { bottle: Bottle, imageCard: CardView ->
        AnalyticsHelper.bottleClicked(bottle.id)
        var array: ByteArray? = null
        val bottleImage = imageCard.findViewById<ImageView>(R.id.bottleImage)
        if (bottleImage.drawable != null) {
            val bStream = ByteArrayOutputStream()
            try {
                (bottleImage.drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream)
            } catch (e: Exception){}
            array = bStream.toByteArray()
        }

        val intent =  ViewBottleActivity.forBottle(activity!!, bottle, array)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                imageCard,
                ViewCompat.getTransitionName(imageCard)!!)
        startActivity(intent, options.toBundle())
    }
}