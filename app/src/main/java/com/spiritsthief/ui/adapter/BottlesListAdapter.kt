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

class BottlesListAdapter(whiskiesList: MutableList<Bottle>,
                         onBottleClick: (Bottle, CardView) -> Unit,
                         pagedListHandler: PagedListHandler?): BaseBottlesListAdapter<Holder>(whiskiesList, onBottleClick, pagedListHandler) {
    override fun onCreateBottleViewHolder(parent: ViewGroup) =
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.bottle_list_item, parent, false), onBottleClick)
}

class Holder(itemView: View, private val onBottleClick: (Bottle, CardView) -> Unit) : BottleHolder(itemView, onBottleClick) {
    override fun bind() {
        itemView.findViewById<TextView>(R.id.name).text = bottle.name
        var bottler = bottle.bottler ?: itemView.context.resources.getString(R.string.bottler) + ": "+itemView.context.resources.getString(R.string.not_available)
        if (!TextUtils.isEmpty(bottle.series)) bottler += " (" + bottle.series + ")"
        itemView.findViewById<TextView>(R.id.bottler).text = bottler
        try {
            itemView.findViewById<TextView>(R.id.avg_price).text = String.format(itemView.context.resources.getString(R.string.bottle_info_avg_price), bottle.currency + String.format("%.0f", bottle.avgPrice!!.toDouble()))
        } catch (e: Exception) {
            var a = 4
            a = a + a
        }
        itemView.findViewById<TextView>(R.id.style).text = bottle.style?.trim() ?: ""
        itemView.findViewById<TextView>(R.id.abv).text = (bottle.abv + "%")
        itemView.findViewById<TextView>(R.id.age).text = if (TextUtils.isEmpty(bottle.age) || bottle.age.equals("0")) itemView.context.getString(R.string.nas) else (bottle.age + "YO")
        itemView.findViewById<TextView>(R.id.size).text = (bottle.size?.replace(".", "") + " ml")

        var cask = ""
        if (!TextUtils.isEmpty(bottle.caskType)) cask += bottle.caskType
        if (!TextUtils.isEmpty(bottle.caskNumber)) cask += " " + bottle.caskNumber
        if (TextUtils.isEmpty(cask)) cask = itemView.context.resources.getString(R.string.not_available)
        itemView.findViewById<TextView>(R.id.cask).text = String.format(itemView.context.resources.getString(R.string.bottle_info_cask), cask)

        val image = itemView.findViewById<ImageView>(R.id.bottleImage)
        image.setImageResource(R.drawable.ic_bottle_place_holder)

        if (bottle.imageUrl!!.isNotEmpty()) {
            GlideApp.with(image.context)
                    .load(bottle.imageUrl!![0])
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            ApiClient.get().reportInvalidImage(InvalidImage(bottle.id.toString(), bottle.imageUrl!![0]))
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