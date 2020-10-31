//package com.spiritsthief.ui.adapter
//
//import androidx.recyclerview.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.TextView
//import com.spiritsthief.R
//import com.spiritsthief.common.GlideApp
//import com.spiritsthief.api.model.Bottle
//import com.spiritsthief.views.SquareImageView
//
///**
// * Created by Dor Ayalon on 1/16/18.
// */
//class BottleCardsAdapter(private val bottles: List<Bottle>, private val onBottleClick: (Bottle, Int) -> Unit): RecyclerView.Adapter<BottleCardsAdapter.BottleCardHolder>() {
//    private val innerOnBottleClick: (Bottle, Int) -> Unit  = { bottle: Bottle, position: Int ->
//        notifyItemChanged(position)
//        if (currentSelected >= 0) notifyItemChanged(currentSelected)
//        currentSelected = position
//        onBottleClick.invoke(bottle, position)
//    }
//    private var currentSelected = -1
//    override fun onBindViewHolder(holder: BottleCardHolder, position: Int) =
//            holder.bind(bottles[position], currentSelected == position)
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottleCardHolder {
//        return BottleCardHolder(LayoutInflater.from(parent.context).inflate(R.layout.bottle_card, parent, false), innerOnBottleClick)
//    }
//
//    fun setSelected(position: Int) {
//        innerOnBottleClick.invoke(bottles[position], position)
//    }
//
//    override fun getItemCount() = bottles.size
//
//    class BottleCardHolder(itemView: View, onBottleClick: (Bottle, Int) -> Unit): RecyclerView.ViewHolder(itemView) {
//        lateinit var bottle: Bottle
//
//        init {
//            itemView.setOnClickListener {
//                onBottleClick.invoke(bottle, adapterPosition)
//            }
//        }
//
//        fun bind(bottle: Bottle, selected: Boolean) {
//            this.bottle = bottle
//            itemView.findViewById<TextView>(R.id.name).text = bottle.name
//            itemView.findViewById<TextView>(R.id.style).text = bottle.style
//            val image = itemView.findViewById<SquareImageView>(R.id.bottle_image)
//            image.setImageBitmap(null)
//            if (bottle.imageUrl != null && bottle.imageUrl.isNotEmpty()) {
//                GlideApp.with(image.context)
//                        .load(bottle.imageUrl[0])
//                        .into(image)
//            }
//
//            itemView.findViewById<FrameLayout>(R.id.frame).setBackgroundResource(
//                    if (selected) R.color.colorPrimaryLight else R.color.white
//            )
//        }
//    }
//}