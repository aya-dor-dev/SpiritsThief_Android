package com.spiritsthief.ui.viewbottle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.spiritsthief.R
import com.spiritsthief.ui.adapter.ImagesAdapter
import com.spiritsthief.ui.adapter.ImagesAdapter2
import com.spiritsthief.ui.adapter.Indicator
import kotlinx.android.synthetic.main.activity_view_full_size.*

class ViewFullSize : AppCompatActivity() {
    companion object {
        private const val ARG_IMAGES = "images"
        private const val ARG_START_POS = "start_pos"
        private const val ARG_BOTTLE_ID = "bottle_id"

        fun startWith(activity: AppCompatActivity, bottleId: String, images: List<String>, startPosition: Int) {
            val bundle = Bundle()
            bundle.putInt(ARG_START_POS, startPosition)
            bundle.putStringArrayList(ARG_IMAGES, ArrayList(images))
            bundle.putString(ARG_BOTTLE_ID, bottleId)
            val intent = Intent(activity, ViewFullSize::class.java)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_size)

        intent.extras?.let {
            val startPosition = it.getInt(ARG_START_POS, 0)
            val images = it.getStringArrayList(ARG_IMAGES)
            val bottleId = it.getString(ARG_BOTTLE_ID)
            pager.adapter = ImagesAdapter2(bottleId, images) {}
            pager.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false).apply {
                scrollToPosition(startPosition)
            }
            LinearSnapHelper().attachToRecyclerView(pager)
            pager.addItemDecoration(Indicator(this, 6))
        }
    }
}
