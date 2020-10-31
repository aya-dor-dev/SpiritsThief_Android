package com.spiritsthief.ui.ui.filter

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.spiritsthief.R
import com.spiritsthief.api.model.SearchRequest
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity: AppCompatActivity(){
    companion object {
        val EXTRA_SEARCH_REQUEST = "search_request"
        fun getBundle(searchRequest: SearchRequest): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_SEARCH_REQUEST, searchRequest)

            return bundle
        }
    }

    lateinit var filterViewModel: FilterViewModel
    lateinit var mainFragment: FilterMainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        filterViewModel.searchRequest = intent.extras.getSerializable(EXTRA_SEARCH_REQUEST) as SearchRequest

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val close = getDrawable(R.drawable.ic_arrow_back_black_24dp)
            close.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
            setHomeAsUpIndicator(close)
        }

        mainFragment = FilterMainFragment()
        supportFragmentManager.beginTransaction().replace(R.id.content, mainFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (supportFragmentManager.backStackEntryCount == 0) {
                onBackPressed()
            } else {
                mainFragment.initAdapter()
                supportFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        val bundle = Bundle()
        bundle.putSerializable(EXTRA_SEARCH_REQUEST, filterViewModel.searchRequest)
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)

        super.finish()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_to_right)
    }
}