package com.spiritsthief.ui.fabmenu

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.spiritsthief.R
import java.util.*

/**
 * Created by Dor Ayalon on 1/4/18.
 */
class FabMenuFragment : Fragment() {

    companion object {
        private val ARG_MENU_ITEMS = "arg_menu_items"
        private val ARG_OVERLAY_BG_COLOR = "arg_overlay_bg"
        private val ARG_RES_ID = "res_id"
    }

    var menuCallback: FabMenuCallback? = null

    lateinit var mItems: List<FabMenuItem>
    var overlayColor = Color.WHITE
    lateinit var overlay: View
    lateinit var itemsContainer: LinearLayout
    var resId = R.layout.sub_fab

    private var onItemClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (menuCallback != null) {
            val itemId = view.tag as Int
            menuCallback!!.onMenuItemSelected(itemId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mItems.isEmpty()) {
            throw MissingFormatArgumentException("Must initialize with menu id")
        }

        return inflater.inflate(R.layout.fragment_fab_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        itemsContainer = view.findViewById(R.id.items_container) as LinearLayout
        overlay = view.findViewById(R.id.overlay)
        overlay.setBackgroundColor(overlayColor)
        overlay.setOnClickListener { menuCallback!!.onOverlayTouch() }

        val inflater = LayoutInflater.from(view.context)

        for (i in mItems.indices) {
            val item = mItems[i]
            val subFab = inflater.inflate(resId, null)
            subFab.setPadding(0, 16, 0, 0)
            subFab.setOnClickListener(onItemClickListener)
            subFab.tag = item.id

            val title = subFab.findViewById(R.id.title) as TextView
            val icon = subFab.findViewById(R.id.sub_fab) as FloatingActionButton

            if (item.title != null) {
                title.visibility = View.VISIBLE
                title.text = item.title
            }

            if (item.icon != -1) {
                icon.visibility = View.VISIBLE
                icon.setImageResource(item.icon)
            }

            itemsContainer.addView(subFab)
            animateFabs(subFab, mItems.size - i - 1)
        }
    }

    private fun animateFabs(fabContainer: View, offset: Int) {
        fabContainer.alpha = 0f

        fabContainer.animate()
                .alpha(1.0f)
                .setDuration(150).startDelay = (100 + offset * 100).toLong()
    }
}