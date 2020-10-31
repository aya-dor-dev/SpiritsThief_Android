package com.spiritsthief.ui.ui.main.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.common.*

class SettingsAdapter(var settings: List<Quadrouple<Int, Int, Int?, String?>>,
                      private val onSettingSelected: (Int) -> Unit): RecyclerView.Adapter<SettingsListHolder>() {

    override fun onBindViewHolder(holder: SettingsListHolder, position: Int) =
            holder.bind(settings[position])

    override fun getItemCount() = settings.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SettingsListHolder(LayoutInflater.from(parent.context).inflate(R.layout.settings_list_item, parent, false), onSettingSelected)
}

class SettingsListHolder(itemView: View,
                         onSettingSelected: (Int) -> Unit): RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    private val value: TextView = itemView.findViewById(R.id.value)
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    var settingCode = -1

    init {
        itemView.setOnClickListener { onSettingSelected(settingCode) }
    }

    fun bind(data: Quadrouple<Int, Int, Int?, String?>) {
        settingCode = data.first

        title.setText(data.second)
        data.third?.let {
            value.visibility = View.GONE
            icon.visibility = View.VISIBLE
            icon.setImageResource(it)
        }

        data.fourth?.let {
            icon.visibility = View.GONE
            value.visibility = View.VISIBLE
            value.text = it
        }
    }
}