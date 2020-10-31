package com.spiritsthief.ui.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.common.Quadrouple


class FilterAdapter(var list: List<Quadrouple<TYPE, Int, String, String>?>,
                    val filterClicked: (Int) -> Unit) : RecyclerView.Adapter<FilterListViewHolder>() {

    enum class TYPE {
        DATA_VALUE,
        TOGGLE
    }

    private val VT_DIVIDER = 0
    private val VT_DATA_VALUE = 1
    private val VT_TOGGLE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterListViewHolder = when (viewType) {
        VT_DIVIDER -> FilterDividerHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_divider, parent, false), filterClicked)
        VT_DATA_VALUE -> FilterMultiValueHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_main_list_item, parent, false), filterClicked)
        else -> FilterToggleHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_main_list_item_toggle, parent, false), filterClicked)
    }


    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        list[position]?.let {
            return when (it.first) {
                TYPE.DATA_VALUE -> VT_DATA_VALUE
                else -> VT_TOGGLE
            }
        }

        return VT_DIVIDER
    }

    override fun onBindViewHolder(holder: FilterListViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

abstract class FilterListViewHolder(itemView: View,
                                    val openFilter: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

    var data: Quadrouple<FilterAdapter.TYPE, Int, String, String>? = null

    init {
        itemView.setOnClickListener {
            data?.let {
                it.fourth?.let {
                    openFilter(this.data!!.second)
                }
            }
        }
    }

    open fun bind(data: Quadrouple<FilterAdapter.TYPE, Int, String, String>?) {}
}

class FilterDividerHolder(itemView: View,
                          openFilter: (Int) -> Unit) : FilterListViewHolder(itemView, openFilter) {

    fun bind() {
        this.data = null
    }
}

class FilterMultiValueHolder(itemView: View,
                             openFilter: (Int) -> Unit) : FilterListViewHolder(itemView, openFilter) {
    override fun bind(data: Quadrouple<FilterAdapter.TYPE, Int, String, String>?) {
        this.data = data

        itemView.findViewById<TextView>(R.id.title).text = data!!.third
        if (data.fourth == null) {
            itemView.findViewById<TextView>(R.id.value).text = ""
            itemView.alpha = 0.3f
            itemView.isEnabled = false
        } else {
            itemView.findViewById<TextView>(R.id.value).text = data.fourth
            itemView.alpha = 1f
            itemView.isEnabled = true
        }
    }
}

class FilterToggleHolder(itemView: View,
                         openFilter: (Int) -> Unit) : FilterListViewHolder(itemView, openFilter) {

    init {
        itemView.setOnClickListener { null }
    }

    override fun bind(data: Quadrouple<FilterAdapter.TYPE, Int, String, String>?) {
        this.data = data

        itemView.findViewById<Switch>(R.id.toggle).apply {
            isChecked = data!!.fourth!!.toBoolean()

            setOnCheckedChangeListener { compoundButton, b ->
                openFilter(data!!.second)
            }
            text = data!!.third
        }
    }
}