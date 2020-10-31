package com.spiritsthief.ui.adapter

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.InvalidImage
import com.spiritsthief.common.GlideApp

class BottlesCollectionListAdapter(whiskiesList: MutableList<Bottle>,
                         onBottleClick: (Bottle, CardView) -> Unit,
                         pagedListHandler: PagedListHandler?): BaseBottlesListAdapter<CollectionBottleHolder>(whiskiesList, onBottleClick, pagedListHandler) {
    override fun onCreateBottleViewHolder(parent: ViewGroup) =
            CollectionBottleHolder(LayoutInflater.from(parent.context).inflate(R.layout.bottle_card, parent, false), onBottleClick)
}

class CollectionBottleHolder(itemView: View, private val onBottleClick: (Bottle, CardView) -> Unit) : BottleHolder(itemView, onBottleClick) {
    init {
        val card = itemView.findViewById<CardView>(R.id.image_card)
        card.post {
            card.radius = (card.width / 2).toFloat()
        }
    }

    override fun bind() {
        itemView.findViewById<TextView>(R.id.name).text = bottle.name

        val image = itemView.findViewById<ImageView>(R.id.bottleImage)
        image.setImageResource(R.drawable.ic_bottle_place_holder)

        if (bottle.imageUrl!!.isNotEmpty()) {
            GlideApp.with(image.context)
                    .load(bottle.imageUrl!![0])
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            ApiClient.get().reportInvalidImage(InvalidImage(bottle.id.toString(), bottle.imageUrl!![0]))
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean) = false
                    })
                    .into(image)
        }

        itemView.setOnClickListener {
            onBottleClick.invoke(bottle, itemView.findViewById(R.id.image_card))
        }
    }
}