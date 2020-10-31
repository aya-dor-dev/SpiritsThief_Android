package com.spiritsthief.ui.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.spiritsthief.R
import com.spiritsthief.common.AnalyticsHelper
import com.spiritsthief.common.SimpleScrollListener
import com.spiritsthief.ui.ui.main.search.SearchFragment
import com.spiritsthief.ui.ui.main.settings.SettingsFragment
import com.spiritsthief.ui.ui.main.wishlist.CollectionFragment
import com.spiritsthief.ui.ui.main.wishlist.WishListFragment
import com.spiritsthief.ui.viewbottle.ViewBottleActivity
import kotlinx.android.synthetic.main.activity_main2.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MainActivity2 : AppCompatActivity(), SimpleScrollListener {

    private val searchFragment = SearchFragment()
    private val settingsFragment = SettingsFragment()
    private val wishListFragment = WishListFragment()
    private val collectionFragment = CollectionFragment()
    private val fragments = listOf(
            searchFragment,
            settingsFragment,
            wishListFragment,
            collectionFragment)

    private var currentFragment: BaseFragment = searchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.extras != null && intent.extras.containsKey(MainActivity2.INTENT_BOTTLE_ID)) {
            intent.extras.getString(MainActivity2.INTENT_BOTTLE_ID)!!.let {
                AnalyticsHelper.viewBottleFromSharedLink(it.toLong())
                ViewBottleActivity.startForID(this, it)
            }
        }

        setContentView(R.layout.activity_main2)

        val ft = supportFragmentManager.beginTransaction()
        fragments.forEach {
            ft.add(R.id.fragment_container, it)
            ft.hide(it)
        }
        ft.commit()
        showFragment(searchFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            if (it.itemId != bottom_navigation.selectedItemId) {
                when (it.itemId) {
                    R.id.search -> showFragment(searchFragment)
                    R.id.settings -> showFragment(settingsFragment)
                    R.id.wish_list -> showFragment(wishListFragment)
                    R.id.collection -> showFragment(collectionFragment)
                }
            }
            true
        }
    }

    private fun showFragment(fragment: BaseFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(currentFragment)
        currentFragment = fragment
        ft.setCustomAnimations(R.anim.fade_in, R.anim.stay)
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.show(fragment)
        ft.commit()
    }

    override fun onScrollUp() {
        if (!KeyboardVisibilityEvent.isKeyboardVisible(this)) {
            bottom_navigation.animate().translationY(0F).setDuration(200).start()
        }
    }

    override fun onScrollDown() {
        bottom_navigation.animate().translationY(bottom_navigation.height.toFloat()).setDuration(200).start()
    }

    override fun scrollTo(x: Int, y: Int) {

    }

    override fun onResume() {
        super.onResume()
        onScrollUp()

    }
    override fun onBackPressed() {
        if (!currentFragment.onBackPressed()) {
            if (bottom_navigation.selectedItemId != R.id.search) {
                bottom_navigation.selectedItemId = R.id.search
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        val INTENT_BOTTLE_ID = "btl_id"

        fun startForBottleId(activity: Activity, bottleId: String) {
            val bundle = Bundle()
            bundle.putString(INTENT_BOTTLE_ID, bottleId)
            val intent = Intent(activity, MainActivity2::class.java)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}