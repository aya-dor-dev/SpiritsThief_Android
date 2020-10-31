package com.spiritsthief.ui

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.spiritsthief.BuildConfig
import com.spiritsthief.R
import com.spiritsthief.common.AnalyticsHelper
import kotlinx.android.synthetic.main.activity_view_in_store.*

class ViewInStoreActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_BOTTLE = "bottle_name"
        private val EXTRA_STORE = "store_name"
        private val EXTRA_URL = "url"

        fun startForBottle(activity: Activity, bottleName: String, storeName: String, url: String)  {
            val intent = Intent(activity, ViewInStoreActivity::class.java)
            val bundle = Bundle()
            bundle.putString(EXTRA_BOTTLE, bottleName)
            bundle.putString(EXTRA_STORE, storeName)
            bundle.putString(EXTRA_URL, url)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.extras.containsKey(EXTRA_BOTTLE) || !intent.extras.containsKey(EXTRA_STORE))
            throw IllegalArgumentException("Missing bottle parameter")

        setContentView(R.layout.activity_view_in_store)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            val arrow = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            arrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
            it.setHomeAsUpIndicator(arrow)
        }

        title = intent.extras.getString(EXTRA_BOTTLE)
        toolbar.subtitle = intent.extras.getString(EXTRA_STORE)

        url = intent.extras.getString(EXTRA_URL)
        loadStore()
    }

    private fun loadStore() {
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()
        webview.settings.javaScriptEnabled = true
        webview.settings.setSupportZoom(true)
        webview.settings.builtInZoomControls = true
        webview.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
    private fun getAdUnitId(): String {
        return when(BuildConfig.DEBUG) {
            true -> "ca-app-pub-3940256099942544/1033173712"
            false -> "ca-app-pub-8991716869725643/4146955779"
        }
    }
}
