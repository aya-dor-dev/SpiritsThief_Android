package com.spiritsthief.ui.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.spiritsthief.R
import com.spiritsthief.api.model.Store
import com.spiritsthief.views.SquareImageView
import android.graphics.drawable.PictureDrawable
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.spiritsthief.api.model.SortedStores
import com.spiritsthief.common.GlideApp
import com.spiritsthief.common.VERIFIED_THRESHOLD_DAYS
import com.spiritsthief.common.getCountryFlagEmoji
import com.spiritsthief.common.glidesvg.SvgSoftwareLayerSetter


/**
 * Created by Dor Ayalon on 12/22/17.
 */
class StoresListAdapter(private val storeList: List<Store>,
                        private val leftOver: Int,
                        private val onStoreClick: (Store) -> Unit,
                        private val onMoreClicked: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VT_STORE = 0
    private val VT_MORE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VT_STORE) {
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.store_list_item, parent, false), onStoreClick)
        } else {
            MoreHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_more, parent, false), onMoreClicked)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != storeList.size) {
            (holder as Holder).bind(storeList[position])
        } else {
            (holder as MoreHolder).bind(leftOver)
        }
    }

    override fun getItemCount(): Int {
        if (leftOver > 0) {
            return storeList.size + 1
        } else {
            return storeList.size
        }
    }


    override fun getItemViewType(position: Int) = if (position == storeList.size) VT_MORE else VT_STORE

    class Holder(itemView: View, private val onStoreClick: (Store) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private lateinit var store: Store

        init {
            itemView.setOnClickListener {
                onStoreClick.invoke(store)
            }
        }

        fun bind(store: Store) {
            this.store = store
            itemView.findViewById<TextView>(R.id.store_name).text = store.name
            itemView.findViewById<TextView>(R.id.price).text = "${store.currency}${store.price}"

            val image = itemView.findViewById<SquareImageView>(R.id.store_image)
            image.setImageResource(R.drawable.ic_online_store)

            val countryFlag = itemView.findViewById<TextView>(R.id.country_flag)
            countryFlag.text = ""

            if (!TextUtils.isEmpty(store.imageUrl)) {
                if (store.imageUrl.endsWith("svg")) {
                    val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(image.context)
                            .`as`(PictureDrawable::class.java)
                            .listener(SvgSoftwareLayerSetter())
                    requestBuilder.load(store.imageUrl).into(image)
                } else {
                    Glide.with(image.context)
                            .load(store.imageUrl)
                            .into(image)

                }
            }

            countryFlag.text = getCountryFlagEmoji(store.countryCode)
            var verifiedIcon = R.drawable.ic_verified
            if (store.lastUpdate != null) {
                val lastUpdate = store.lastUpdate.toInt()
                if (lastUpdate > VERIFIED_THRESHOLD_DAYS) {
                    verifiedIcon = R.drawable.ic_unverified
                }
            } else {
                verifiedIcon = R.drawable.ic_unverified
            }

            itemView.findViewById<ImageView>(R.id.verified).setImageResource(verifiedIcon)

            if (store.valid == 0) {
                itemView.findViewById<ImageView>(R.id.verified).setImageResource(R.drawable.ic_sold_out)
                itemView.findViewById<TextView>(R.id.price).visibility = View.GONE
            }
        }
    }

    class MoreHolder(itemView: View, private val onMoreClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { onMoreClick.invoke() }
        }

        fun bind(left: Int) {
            itemView.findViewById<TextView>(R.id.more).text = "$left More"
        }
    }
}