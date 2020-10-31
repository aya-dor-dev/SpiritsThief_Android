package com.spiritsthief.ui.ui.main.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spiritsthief.R
import com.spiritsthief.api.model.Category

/**
 * Created by dorayalon on 20/03/2018.
 */
class CategoryAdapter(var selected: String,
                      val onSelected: (String, Int) -> Unit): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    val categories = listOf(
            Category("Whisky", listOf(), R.drawable.ic_category_whisky),
            Category("Rum", listOf(), R.drawable.ic_category_rum),
            Category("Gin", listOf(), R.drawable.ic_category_gin),
            Category("Cognac", listOf(), R.drawable.ic_category_cognac),
            Category("Vodka", listOf(), R.drawable.ic_category_vodka),
            Category("Other Spirits", listOf(), R.drawable.ic_more_vert_black_24dp))
    private val VT_SELECTED = 0
    private val VT_UNSELECTED = 1
    private val VT_EMPTY = 2

    private val onCategorySelected: (String, Int) -> Unit = { category: String, position: Int ->
        changeSelected(category)
        onSelected.invoke(selected, position)
    }

    fun changeSelected(value: String?): Int {
        value?.let {
            selected = it
            notifyDataSetChanged()
            return categories.map { it.name }.indexOf(it)
        }

        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CategoryHolder(LayoutInflater.from(parent.context).inflate(when (viewType) {
                VT_SELECTED -> R.layout.type_list_item_selected
                VT_EMPTY -> R.layout.type_list_item_empty
                else -> R.layout.type_list_item
            }, parent, false), onCategorySelected)

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemViewType(position: Int) = when(categories[position].name) {
        "" -> VT_EMPTY
        selected -> VT_SELECTED
        else -> VT_UNSELECTED
    }

    open class CategoryHolder(itemView: View,
                              private val onCategorySelected: (String, Int) -> Unit): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onCategorySelected.invoke((itemView as TextView).text.toString(), adapterPosition)
            }
        }
        open fun bind(category: Category) {
            (itemView as TextView).text = category.name
            (itemView as TextView).setCompoundDrawablesWithIntrinsicBounds(category.iconResId, 0, 0, 0)
        }
    }

    class EmptyHolder(itemView: View): CategoryHolder(itemView, { s: String, i: Int -> }) {
        override fun bind(category: Category) {
        }
    }
}