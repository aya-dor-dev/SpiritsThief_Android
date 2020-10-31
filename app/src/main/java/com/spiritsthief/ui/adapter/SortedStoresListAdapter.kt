package com.spiritsthief.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.api.model.SortedStores
import com.spiritsthief.api.model.Store
import com.spiritsthief.common.getCountryFlagEmoji


/**
 * Created by Dor Ayalon on 12/22/17.
 */
class SortedStoresListAdapter(sortedStores: SortedStores,
                              private val onStoreClick: (Store) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VT_STORE = 0
    private val VT_HEADER = 1
    private val data = mutableListOf<Any>()

    init {
        if (sortedStores.verified.isNotEmpty()) {
            data.add(Pair(R.string.verified, R.drawable.ic_verified))
            data.addAll(sortedStores.verified)
        }
        if (sortedStores.unverified.isNotEmpty()) {
            data.add(Pair(R.string.unverified, R.drawable.ic_unverified))
            data.addAll(sortedStores.unverified)
        }
        if (sortedStores.soldout.isNotEmpty()) {
            data.add(Pair(R.string.sold_out, R.drawable.ic_sold_out))
            data.addAll(sortedStores.soldout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VT_STORE) {
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.store_list_row, parent, false), onStoreClick)
        } else {
            ListHeader(LayoutInflater.from(parent.context).inflate(R.layout.list_header, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (data[position] is Pair<*, *>) {
            (holder as ListHeader).bind(data[position] as Pair<Int, Int>)
        } else {
            (holder as Holder).bind(data[position] as Store)
        }
    }

    override fun getItemCount() = data.size


    override fun getItemViewType(position: Int) = if (data[position] is Pair<*,*>) VT_HEADER else VT_STORE

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
            if (store.valid == 1) {
                itemView.findViewById<TextView>(R.id.count).text = "${store.currency}${store.price.split(".")[0]}"
            }

            itemView.findViewById<TextView>(R.id.flag).text = getCountryFlagEmoji(store.countryCode)
        }
    }

    class ListHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(headerResId: Pair<Int, Int>) {
            (itemView as TextView).apply {
                text = itemView.context.resources.getString(headerResId.first)
                setCompoundDrawablesWithIntrinsicBounds(headerResId.second, 0, 0, 0)
            }
        }
    }
}
