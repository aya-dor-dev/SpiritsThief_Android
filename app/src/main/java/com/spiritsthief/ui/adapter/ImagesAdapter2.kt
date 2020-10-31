package com.spiritsthief.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.InvalidImage
import com.spiritsthief.common.GlideApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImagesAdapter2(private val bottleId: String,
                     private var images: MutableList<String>,
                     private var onImageSelected: (Int) -> Unit?) : RecyclerView.Adapter<ImageHolder>() {

    private var overrideSize = -1

    private val onInvalidImage: (String) -> Unit = { url ->
        images.indexOf(url).let { index ->
            if (index != -1) {
                images.removeAt(index)
                notifyItemRemoved(index)
                GlobalScope.launch(Dispatchers.IO) {
                    ApiClient.get().reportInvalidImage(InvalidImage(bottleId, url)).execute()
                }
            }
        }
    }

    fun overrideSize(size: Int): ImagesAdapter2 {
        this.overrideSize = size
        return this
    }

    override fun getItemCount() = images.count()

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.bind(images[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageHolder(ImageView(parent.context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }, overrideSize, onImageSelected, onInvalidImage)
}

class ImageHolder(itemView: View,
                  private val overrideSize: Int,
                  private val onImageSelected: (Int) -> Unit?,
                  private val onInvalidImage: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener { onImageSelected(adapterPosition) }
    }

    fun bind(url: String) {
        var request = GlideApp.with(itemView.context)
                .load(url)

        request = request.apply(RequestOptions().override(overrideSize))
//        if (overrideSize > -1) {
//            request.apply(RequestOptions().override(resources.getDimensionPixelSize(R.dimen.gallery_size), resources.getDimensionPixelSize(R.dimen.gallery_size)))
//        }

        request.listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                onInvalidImage(url)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean) = false
        }).into(itemView as ImageView)
    }
}

class Indicator(ctx: Context, indicatorSize: Int = 2): RecyclerView.ItemDecoration() {
    private val colorActive = ctx.resources.getColor(R.color.colorPrimaryDark)
    private val colorInactive = ctx.resources.getColor(R.color.colorPrimary)

    private val DP = Resources.getSystem().displayMetrics.density

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private val mIndicatorHeight = 0F

    /**
     * Indicator stroke width.
     */
    private val mIndicatorStrokeWidth = (DP * indicatorSize)

    /**
     * Indicator width.
     */
    private val mIndicatorItemLength = DP * indicatorSize
    /**
     * Padding between indicators.
     */
    private val mIndicatorItemPadding = DP * 2 * indicatorSize

    /**
     * Some more natural animation interpolation
     */
    private val mInterpolator = AccelerateDecelerateInterpolator()

    private val mPaint = Paint()

    init {
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = mIndicatorStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount = parent.adapter?.itemCount ?: 0

        // center horizontally, calculate width and subtract half from center
        val totalLength = mIndicatorItemLength * itemCount
        val paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2F

        // center vertically in the allotted space
        val indicatorPosY = parent.height - mIndicatorHeight / 2F - 8 * DP

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)


        // find active page (which should be highlighted)
        val layoutManager =  parent.layoutManager as LinearLayoutManager
        val activePosition = layoutManager.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        // find offset of active page (if the user is scrolling)
        val activeChild = layoutManager.findViewByPosition(activePosition)!!
        val left = activeChild.left
        val width = activeChild.width

        // on swipe the active item will be positioned from [-width, 0]
        // interpolate offset for smooth animation
        val progress = mInterpolator.getInterpolation(left * -1 /  width.toFloat())

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)
    }

    private fun drawInactiveIndicators(c: Canvas, indicatorStartX: Float, indicatorPosY: Float, itemCount: Int) {
        mPaint.color = colorInactive

        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding

        var start = indicatorStartX

        for (i: Int in 0 until itemCount) {
            // draw the line for every item
            c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint)
            start += itemWidth
        }
    }

    private fun drawHighlights(c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
                               highlightPosition: Int, progress: Float, itemCount: Int) {
        mPaint.color = colorActive

        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding

        if (progress == 0F) {
            // no swipe, draw a normal indicator
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            c.drawLine(highlightStart, indicatorPosY,
                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint)
        } else {
            var highlightStart = indicatorStartX + itemWidth * highlightPosition
            // calculate partial highlight
            val partialLength = mIndicatorItemLength * progress

            // draw the cut off highlight
            c.drawLine(highlightStart + partialLength, indicatorPosY,
                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint)

            // draw the highlight overlapping to the next item as well
            if (highlightPosition < itemCount - 1) {
                highlightStart += itemWidth
                c.drawLine(highlightStart, indicatorPosY,
                        highlightStart + partialLength, indicatorPosY, mPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mIndicatorHeight.toInt()
    }
}