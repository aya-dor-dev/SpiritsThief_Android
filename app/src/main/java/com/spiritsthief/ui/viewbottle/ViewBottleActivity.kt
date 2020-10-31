package com.spiritsthief.ui.viewbottle

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.*
import com.spiritsthief.common.*
import com.spiritsthief.repository.UserCollections
import com.spiritsthief.ui.ViewInStoreActivity
import com.spiritsthief.ui.adapter.ImagesAdapter2
import com.spiritsthief.ui.adapter.Indicator
import com.spiritsthief.ui.adapter.SortedStoresListAdapter
import com.spiritsthief.ui.adapter.StoresListAdapter
import com.spiritsthief.ui.scanner.barcode.BarcodeCaptureActivity
import com.spiritsthief.ui.ui.common.showDialog
import com.spiritsthief.viewmodel.ShopLinksViewModel
import com.spiritsthief.views.BottleDataRecord
import com.spiritsthief.views.Divider
import kotlinx.android.synthetic.main.activity_view_bottle.*
import kotlinx.android.synthetic.main.bottle_view_actions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by Dor Ayalon on 12/20/17.
 */
class ViewBottleActivity : AppCompatActivity() {
    private val REQ_SCAN_BARCODE = 1112

    val onStoreClick: (Store) -> Unit = {
        AnalyticsHelper.storeClicked(bottle.id, it.name, it.url)
        ViewInStoreActivity.startForBottle(this, bottle.name, it.name, it.url)
    }

    companion object {
        private val EXTRA_BOTTLE = "btl"
        private val EXTRA_IMAGE = "image"
        private val EXTRA_BOTTLE_ID = "btl_id"

        fun forBottle(activity: Activity, bottle: Bottle, image: ByteArray?): Intent {
            val intent = Intent(activity, ViewBottleActivity::class.java)
            val bundle = Bundle()
            if (image != null) {
                bundle.putByteArray(EXTRA_IMAGE, image)
            }
            bundle.putSerializable(EXTRA_BOTTLE, bottle)
            intent.putExtras(bundle)

            return intent
        }

        fun startForID(activity: Activity, id: String) {
            val intent = Intent(activity, ViewBottleActivity::class.java)
            val bundle = Bundle()
            bundle.putString(EXTRA_BOTTLE_ID, id)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    private var transitionedIn = false
    private lateinit var uiHandler: Handler
    private var menu: Menu? = null
    private lateinit var bottle: Bottle
    private var originalImage: Bitmap? = null
    private var inWishList = false
    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_bottle)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            with(it) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                (bottle_name.layoutParams as Toolbar.LayoutParams).marginEnd += DP(16).toInt()
            }
        }

        uiHandler = Handler(Looper.getMainLooper())

        when {
            intent.extras.containsKey(EXTRA_BOTTLE) -> {
                bottle = intent.extras.getSerializable(EXTRA_BOTTLE) as Bottle
                val image = intent.extras.getByteArray(EXTRA_IMAGE)
                if (image != null && image.isNotEmpty()) {
                    GlideApp.with(this)
                            .load(image)
                            .into(image_transition)
                } else {
                    image_transition.setImageResource(R.drawable.ic_bottle_place_holder)
                }

                window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
                    override fun onTransitionStart(transition: Transition?) {
                        ObjectAnimator
                                .ofFloat(image_transition_card, "radius", DP(110))
                                .setDuration(300L)
                                .start()
                    }

                    override fun onTransitionEnd(transition: Transition?) {
                        transitionedIn = true

                        if (image != null && image.isNotEmpty()) {
                            image_transition_card.animate().alpha(0.0f).setDuration(150).start()
                            gallery_container.animate().alpha(1.0f).setDuration(150).start()
                        }
                        window.sharedElementEnterTransition.removeListener(this)
                    }
                })

                startWithBottle()
            }
            intent.extras.containsKey(EXTRA_BOTTLE_ID) -> {
                image_transition_card.visibility = View.GONE
                loadBottle(intent.extras.getString(EXTRA_BOTTLE_ID))
            }
            else -> throw IllegalArgumentException("Missing bottle parameter")
        }

        wish_list.setOnClickListener { addToWishList() }

        collection.setOnClickListener { addToCollection() }

        share.setOnClickListener {
            AnalyticsHelper.bottleShared(bottle.id)
            val link = "https://www.thespiritthief.com/bottle/${bottle.id}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey! I found a bottle you might be interested in.\n$link")
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        title = ""

        val sharedElementEnterTransition = window.sharedElementEnterTransition
        sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
            override fun onTransitionEnd(p0: Transition?) {
                uiHandler.postDelayed({
                    if (bottle.imageUrl != null && bottle.imageUrl!!.isNotEmpty()) {
                    }

                    nested_scroll.scrollTo(0, 0)
                }, 100)

            }
        })

        sheetBehavior = BottomSheetBehavior.from(sorted_list_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(view: View, state: Int) {
                card_shaddow.animate().alpha(when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> 0.0f
                    else -> 1.0f
                }).setDuration(300L).start()

                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    showActions()
                } else if (state == BottomSheetBehavior.STATE_HALF_EXPANDED ||
                        state == BottomSheetBehavior.STATE_EXPANDED) {
                    hideActions()
                }

            }
        })
    }

    fun showActions() {
        actions.animate().alpha(1.0f).setDuration(300L).start()
    }

    fun hideActions() {
        actions.animate().alpha(0.0f).setDuration(300L).start()
    }

    private fun loadBottle(id: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.loading_bottle))
        progressDialog.setCancelable(false)
        progressDialog.show()

        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            var btl: Bottle? = null

            try {
                val searchRequest = SearchRequest().apply {
                    this.id.add(id.toLong())
                }
                btl = ApiClient.get().getBottles(searchRequest).execute().body()?.results?.get(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (btl != null) {
                bottle = btl

                uiHandler.post {
                    progressDialog.dismiss()
                    startWithBottle()
                }
            }
        }
    }

    private fun startWithBottle() {
        inWishList = UserCollections.isInWishList(bottle.id)
        setFabIcon()
        populate()

        bottle_name.text = bottle.name
//        bottle_style.text = bottle.style

        val adapter = ImagesAdapter2(bottle.id.toString(), bottle.imageUrl ?: mutableListOf()) {
            ViewFullSize.startWith(this@ViewBottleActivity, bottle.id.toString(), bottle.imageUrl
                    ?: listOf(), it)
        }
        adapter.overrideSize(resources.getDimensionPixelSize(R.dimen.gallery_size))
        images_pager.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(images_pager)
        images_pager.adapter = adapter
        images_pager.addItemDecoration(Indicator(this))

//        indicator.setViewPager(images_pager)
    }

    private fun populate() {
        val infoList = mutableListOf<Pair<String, String>?>()
        resources.apply {
            infoList.add(Pair(getString(R.string.brand), getValue(bottle.distillery)))
            infoList.add(Pair(getString(R.string.country), getValue(bottle.country)))
            infoList.add(Pair(getString(R.string.region), getValue(bottle.region)))
            infoList.add(Pair(getString(R.string.bottler), getValue(bottle.bottler)))
            infoList.add(Pair(getString(R.string.series), getValue(bottle.series)))
            infoList.add(null)
            infoList.add(Pair(getString(R.string.age), if (bottle.age.equals("0")) getString(R.string.nas) else bottle.age!!))
            infoList.add(Pair(getString(R.string.vintage), getValue(bottle.vintage)))
            infoList.add(Pair(getString(R.string.bottling_date), getValue(if (bottle.bottlingDate != null) bottle.bottlingDate.toString() else "")))
            infoList.add(Pair(getString(R.string.abv), getValue(bottle.abv)))
            infoList.add(Pair(getString(R.string.size), getValue(bottle.size)))
            infoList.add(null)
            infoList.add(Pair(getString(R.string.cask_type), getValue(bottle.caskType)))
            infoList.add(Pair(getString(R.string.cask_number), getValue(bottle.caskNumber)))
            infoList.add(Pair(getString(R.string.cask_refill), getListedSelection(bottle.caskWood!!, "-")))
            infoList.add(Pair(getString(R.string.cask_size), getListedSelection(bottle.caskSize!!, "-")))
        }

        store_loading_progress_bar.visibility = View.VISIBLE
        subscribeToShopsList()

        infoList.forEach {
            if (it != null) {
                bottle_info_container.addView(BottleDataRecord(this, it.first, it.second))
            } else {
                bottle_info_container.addView(Divider(this))
            }
        }
    }

    private fun subscribeToShopsList() {
        val shopLinksViewModel = ViewModelProviders.of(this).get(ShopLinksViewModel::class.java)

        val shopsObserver: Observer<SortedStores> = Observer {
            val listToDisplay = mutableListOf<Store>()
            stores_list.layoutManager = LinearLayoutManager(this@ViewBottleActivity, LinearLayoutManager.HORIZONTAL, false)
            if (it.verified.isNotEmpty()) {
                listToDisplay.addAll(it.verified)
                verified_message.text = resources.getString(R.string.verified_message)
                verified_icon.setImageResource(R.drawable.ic_verified)
            } else if (it.unverified.isNotEmpty()) {
                listToDisplay.addAll(it.unverified)
                verified_message.text = resources.getString(R.string.unverified_message)
                verified_icon.setImageResource(R.drawable.ic_unverified)
            } else {
                listToDisplay.addAll(it.soldout)
                verified_message.setText(R.string.sold_out_message)
                verified_icon.setImageResource(R.drawable.ic_sold_out)
            }

            val adapter = StoresListAdapter(listToDisplay, it.count() - listToDisplay.size, onStoreClick, {
                sorted_list.adapter = SortedStoresListAdapter(it, onStoreClick)
                sorted_list.layoutManager = LinearLayoutManager(this@ViewBottleActivity)
                sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                AnalyticsHelper.moreStoresClicked(bottle.id)
            })
            store_loading_progress_bar.animate().alpha(0f).setDuration(150).start()
            stores_list.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_left)
            stores_list.alpha = 1f

            if (it.count() > 1) findViewById<TextView>(R.id.stores_header).append(" (${it.count()})")
            stores_list.adapter = adapter
        }

        shopLinksViewModel.shops.observe(this, shopsObserver)
        shopLinksViewModel.getStoresForBottleId(bottle.id)
    }

    private fun addToWishList() {
        val added = UserCollections.addOrRemoveToWishList(bottle.id)
        setFabIcon()
        Snackbar.make(findViewById(R.id.container),
                if (added) R.string.wish_list_added else R.string.wish_list_removed,
                Snackbar.LENGTH_LONG).show()
    }

    private fun addToCollection() {
        val added = UserCollections.addOrRemoveToCollection(bottle.id)
        setFabIcon()
        Snackbar.make(findViewById(R.id.container),
                if (added) R.string.collection_added else R.string.collection_removed,
                Snackbar.LENGTH_LONG).show()

        if (added && TextUtils.isEmpty(bottle.barcode)) {
            AnalyticsHelper.scanBarcodeDialogDisplayed()
            showDialog(0, R.string.missing_barcode_request, R.drawable.ic_beg, R.string.sure, R.string.not_now, {
                AnalyticsHelper.scanBarcodeDialogUserAction(bottle.id, true)
                startActivityForResult(Intent(this, BarcodeCaptureActivity::class.java), REQ_SCAN_BARCODE)
            }, {
                AnalyticsHelper.scanBarcodeDialogUserAction(bottle.id, false)
            })
        }
    }

    private fun setFabIcon() {
        wish_list.icon.setImageResource(if (UserCollections.isInWishList(bottle.id)) R.drawable.ic_favorite_added else R.drawable.ic_favorite_border_red_24dp)
        collection.icon.setImageResource(if (UserCollections.isInCollection(bottle.id)) R.drawable.ic_collected else R.drawable.ic_collection_small)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun supportNavigateUpTo(upIntent: Intent) {
        onBackPressed()
        super.supportNavigateUpTo(upIntent)
    }

    override fun onBackPressed() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        if (transitionedIn) {
            image_transition_card.animate().setDuration(300L).alpha(1.0f).setListener(object : SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    finishFromTransition()
                }
            })
        } else {
            super.onBackPressed()
        }
    }

    private fun finishFromTransition() {
        window.sharedElementReturnTransition.addListener(object : SimpleTransitionListener() {
            override fun onTransitionStart(transition: Transition?) {
                ObjectAnimator
                        .ofFloat(image_transition_card, "radius", DP(1))
                        .setDuration(300L)
                        .start()
            }
        })
        finishAfterTransition()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_SCAN_BARCODE -> {
                    val barcode: Barcode = data!!.extras.getParcelable(BarcodeCaptureActivity.BarcodeObject) as Barcode
                    updateBarcode(barcode.rawValue)
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    protected fun updateBarcode(barcode: String) = GlobalScope.launch(Dispatchers.Main) {
        val progressDialog = ProgressDialog(this@ViewBottleActivity)
        progressDialog.setMessage(getString(R.string.updating_barcode))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val body = UpdateBarcodeRequest(bottle.id.toString(), barcode)
        val res = try {
            ApiClient.get().updateBarcode(body).await()
        } catch (e: Exception) {
            null
        }

        progressDialog.dismiss()
        res?.code()?.let {
            if (it == 200) {
                AnalyticsHelper.userScanedBarcode(bottle.id, barcode)
                showDialog(0, R.string.thank_you, R.drawable.ic_thank, 3000L)
            }
        }
    }

    private fun getValue(txt: String?): String = if (TextUtils.isEmpty(txt)) "-" else txt!!
}
