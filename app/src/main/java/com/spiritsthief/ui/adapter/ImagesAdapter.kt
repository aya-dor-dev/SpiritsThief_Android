package com.spiritsthief.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.InvalidImage
import com.spiritsthief.common.GlideApp


/**
 * Created by dorayalon on 07/02/2018.
 */
class ImagesAdapter(fm: FragmentManager,
                    private val bottleId: String,
                    private var images: MutableList<String>?,
                    private val originalImage: Bitmap?,
                    private var onImageSelected: (Int) -> Unit?) : FragmentStatePagerAdapter(fm) {
    private var overrideSize = -1

    val onInvalidImage: (String) -> Unit = {
        images?.remove(it)
        notifyDataSetChanged()
    }

    init {
        if (images == null) images = mutableListOf()
    }

    fun overrideSize(size: Int): ImagesAdapter {
        this.overrideSize = size
        return this
    }

    override fun finishUpdate(container: ViewGroup) {
        try {
            super.finishUpdate(container)
        } catch (nullPointerException: NullPointerException) {
            println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate")
        }
    }

    override fun getItem(position: Int): Fragment {
        val frag = ImageFragment().overrideSize(overrideSize)
        frag.imageUrl = images!![position]
        frag.bottleId = bottleId
        if (position == 0) {
            frag.setBitmap(originalImage)
        }
        frag.setListener(position, onImageSelected, onInvalidImage)
        return frag
    }

    override fun getCount() = images!!.size


    class ImageFragment : Fragment() {
        private var overrideSize = -1
        private var originalImage: Bitmap? = null
        var imageUrl: String? = null
        var bottleId: String = ""
        private lateinit var onImageSelected: (Int) -> Unit?
        private lateinit var onInvalidImage: (String) -> Unit?
        var position: Int = 0

        fun overrideSize(size: Int): ImageFragment {
            this.overrideSize = size
            return this
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val imageView = ImageView(activity!!)
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.transitionName = resources.getString(R.string.transition_bottle_image)


            if (imageUrl != null && imageUrl!!.isNotEmpty()) {
                val request = GlideApp.with(this)
                        .load(imageUrl)

                if (overrideSize > -1) {
                    request.apply(RequestOptions().override(resources.getDimensionPixelSize(R.dimen.gallery_size), resources.getDimensionPixelSize(R.dimen.gallery_size)))
                }

                request.listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                        ApiClient.get().reportInvalidImage(InvalidImage(bottleId, imageUrl!!)).enqueue()
//                        onInvalidImage(imageUrl!!)
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean) = false
                }).into(imageView)
            }

            imageView.setOnClickListener {
                onImageSelected.invoke(position)
            }

            return imageView
        }

        fun setListener(pos: Int, onImageSelected: (Int) -> Unit?, onInvalidImage: (String) -> Unit) {
            this.onImageSelected = onImageSelected
            this.position = pos
            this.onInvalidImage = onInvalidImage
        }

        fun setBitmap(image: Bitmap?) {
            this.originalImage = image
        }
    }

}