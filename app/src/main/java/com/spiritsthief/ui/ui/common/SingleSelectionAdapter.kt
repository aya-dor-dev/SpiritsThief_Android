package com.spiritsthief.ui.ui.common

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.spiritsthief.R
import com.spiritsthief.views.CustomCheckBox

/**
 * Created by dorayalon on 01/02/2018.
 */
class SingleSelectionAdapter(val options: MutableList<String?>,
                             var selected: String,
                             onSelectionChanged: (String) -> Unit) : RecyclerView.Adapter<SingleSelectionAdapter.OptionHolder>(), Filterable {
    private val VT_DIVIDER = 0
    private val VT_ITEM = 1


    init {
        options.remove(selected)
        options.add(0, selected)
    }

    private val onItemSelected: (Int, Boolean) -> Unit = { pos: Int, _: Boolean ->
        selected = when(options[pos]) {
            null -> options[pos-1]!!
            else -> options[pos]!!
        }

        notifyDataSetChanged()
        onSelectionChanged.invoke(selected)
    }

    var filteredOptions: List<String?> = options.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        VT_ITEM -> OptionDataHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_checkbox_item, parent, false), onItemSelected)
        else -> Divider(LayoutInflater.from(parent.context).inflate(R.layout.divider, parent, false))
    }


    override fun getItemViewType(position: Int) = when (options[position]) {
        null -> VT_DIVIDER
        else -> VT_ITEM
    }

    override fun getItemCount() = filteredOptions.size

    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        if (holder is OptionDataHolder) {
            holder.bind(filteredOptions[position]!!, selected.contains(filteredOptions[position]!!))
        }
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val q = constraint.toString().toLowerCase()
            val newList = mutableListOf<String?>()
            for (item in options) {
                if (item == null) {
                    newList.add(item)
                } else if (item.toLowerCase().contains(q)) {
                    newList.add(item)
                }
            }

            results.values = newList
            results.count = newList.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredOptions = results.values as List<String>
            notifyDataSetChanged()
        }
    }

    open class OptionHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    class OptionDataHolder(itemView: View,
                        private val onItemSelected: (Int, Boolean) -> Unit) :OptionHolder(itemView) {

        fun bind(option: String, selected: Boolean) {
            val cb = itemView as CustomCheckBox
            cb.onCheckChanged = {}
            cb.text.text = option
            cb.setIsChecked(selected)

            cb.onCheckChanged = { isChecked -> onItemSelected.invoke(adapterPosition, isChecked) }
        }
    }

    class Divider(itemView: View): OptionHolder(itemView)
}