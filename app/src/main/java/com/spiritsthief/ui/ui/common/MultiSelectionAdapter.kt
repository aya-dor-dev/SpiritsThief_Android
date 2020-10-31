package com.spiritsthief.ui.ui.common

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.spiritsthief.R
import com.spiritsthief.views.CustomCheckBox

/**
 * Created by dorayalon on 24/01/2018.
 */
class MultiSelectionAdapter(var options: ArrayList<String>,
                            val selected: ArrayList<String>,
                            onSelectionChanged: (List<String>) -> Unit): RecyclerView.Adapter<MultiSelectionAdapter.OptionHolder>(), Filterable {

    private val onItemSelected: (Int, Boolean) -> Unit = {pos: Int, isSelected: Boolean ->
        if (pos != 0) {
            val item = filteredOptions[pos - 1]
            if (isSelected) selected.add(item) else selected.remove(item)
            notifyItemChanged(pos)
        } else {
            selected.clear()
            notifyDataSetChanged()
        }

        notifyItemChanged(0)
        onSelectionChanged(selected)
    }

    var filteredOptions: List<String>
    var query = ""

    init {
        val tmpSelected = options.filter { selected.contains(it) }
        val tmpUnSelected = options.filter { !selected.contains(it) }
        options.clear()
        options.addAll(tmpSelected)
        options.addAll(tmpUnSelected)
        filteredOptions = options.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            OptionHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_checkbox_item, parent, false), onItemSelected)

    override fun getItemCount() = filteredOptions.size + 1

    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        if (position == 0) holder.bind("Any", selected.isEmpty(), "") else holder.bind(filteredOptions[position - 1], selected.contains(filteredOptions[position - 1]), query)
    }

    override fun getFilter() =  object: Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            query = constraint.toString()
            val results = FilterResults()
            val q = constraint.toString().toLowerCase()
            val newList = mutableListOf<String>()
            for (item in options) {
                if (item.toLowerCase().contains(q)) {
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

    class OptionHolder(itemView: View,
                       private val onItemSelected: (Int, Boolean) -> Unit): RecyclerView.ViewHolder(itemView) {

        fun bind (option: String, selected: Boolean, query: String) {
            val cb = itemView as CustomCheckBox
            cb.onCheckChanged = {}

            val spannableString = SpannableString(option)
            val start = option.indexOf(query, 0, true)
            if (start >= 0) {
                spannableString.setSpan(StyleSpan(Typeface.BOLD), start, start + query.length, 0)
                spannableString.setSpan(ForegroundColorSpan(itemView.context.resources.getColor(R.color.dark_gray)), start, start + query.length, 0)
            }

            cb.text.text = spannableString
            cb.setIsChecked(selected)

            cb.onCheckChanged = {  isChecked ->  onItemSelected.invoke(adapterPosition, isChecked)}
        }
    }

}