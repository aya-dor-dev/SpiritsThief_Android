package com.spiritsthief.common

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.spiritsthief.SpiritsThiefApplication
import com.spiritsthief.api.model.SearchRequest

/**
 * Created by Dor Ayalon on 12/26/17.
 */
object AnalyticsHelper {
    private val E_USER_ADDED_BOTTLE_TO_COLLECTION = "collection_bottle_added"
    private val E_USER_REMOVED_BOTTLE_TO_COLLECTION = "collection_bottle_removed"
    private val E_USER_ADDED_BOTTLE_TO_WISHLIST = "wishlist_bottle_added"
    private val E_USER_REMOVED_BOTTLE_TO_WISHLIST = "wishlist_bottle_removed"
    private val E_SCAN_BARCODE_DIALOG_DISPLAYED = "request_barcode_dialog_displayed"
    private val E_SCAN_BARCODE_USER_AGREED = "request_scan_barcode_user_agreed"
    private val E_SCAN_BARCODE_USER_DECLINED = "request_scan_barcode_user_declined"
    private val E_SCAN_BARCODE_USER_UPDATED_BARCODE = "user_updated_barcode"
    private val E_SEARCH_FREE_TEXT = "search_free_text"
    private val E_SEARCH_APPLY_FILTER = "search_apply_filter"
    private val E_SEARCH_BARCODE_SCAN = "search_scan_barcode"
    private val E_SEARCH_RESULTS_RECEIVED = "search_received_results"
    private val E_BOTTLE_CLICKED = "bottle_clicked"
    private val E_SHARE_BOTTLE = "bottle_shared"
    private val E_STORE_CLICKED = "store_clicked"
    private val E_MORE_STORES_CLICKED = "more_stores_clicked"
    private val E_OPEN_BOTTLE_FROM_SHARED_LINK = "view_bottle_from_shared_link"

    private val F_BOTTLE_ID = "bottle_id"
    private val F_STORE_NAME = "store_name"
    private val F_URL = "url"
    private val F_COLLECTION_SIZE = "collection_size"
    private val F_WISHLIST_SIZE = "wishlist_size"
    private val F_AGREE_TO_SCAN = "agree_to_scan"
    private val F_BARCODE = "barcode"
    private val F_SEARCH_QUERY = "search_query"
    private val F_RESULT_COUNT = "results_count"

    fun setDeliveryCountry(country: String) = FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).setUserProperty("delivery_country", country)

    fun setCurrency(currency: String) = FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).setUserProperty("currency", currency)

    fun performSearch(searchRequest: SearchRequest) {
        if (searchRequest.barcode.isNotEmpty()) {
            searchByBarcodeScan(searchRequest)
        } else {
            searchByFreeText(searchRequest)
        }
    }

    private fun searchByFreeText(searchRequest: SearchRequest) {
        val bundle = Bundle()
        bundle.putString(F_SEARCH_QUERY, Gson().toJson(searchRequest))

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SEARCH_FREE_TEXT, bundle)
    }

    private fun searchByBarcodeScan(searchRequest: SearchRequest) {
        val bundle = Bundle()
        bundle.putString(F_BARCODE, searchRequest.barcode)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SEARCH_FREE_TEXT, bundle)
    }

    fun searchApplyFilter(searchRequest: SearchRequest) {
        val bundle = Bundle()
        bundle.putString(F_SEARCH_QUERY, Gson().toJson(searchRequest))

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SEARCH_APPLY_FILTER, bundle)
    }

    fun searchResultsReceived(searchRequest: SearchRequest, count: Int) {
        val bundle = Bundle()
        bundle.putString(F_SEARCH_QUERY, Gson().toJson(searchRequest))
        bundle.putInt(F_RESULT_COUNT, count)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SEARCH_APPLY_FILTER, bundle)
    }

    fun bottleClicked(bottleId: Long) {
        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_BOTTLE_CLICKED, bundle)
    }

    fun bottleShared(bottleId: Long) {
        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SHARE_BOTTLE, bundle)
    }

    fun storeClicked(bottleId: Long, storeName: String, url: String) {
       val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)
        bundle.putString(F_STORE_NAME, storeName)
        bundle.putString(F_URL, url)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_STORE_CLICKED, bundle)
    }

    fun moreStoresClicked(bottleId: Long) {
        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_MORE_STORES_CLICKED, bundle)
    }

    fun viewBottleFromSharedLink(bottleId: Long) {
        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_OPEN_BOTTLE_FROM_SHARED_LINK, bundle)
    }

    fun userUpdatedCollection(bottleId: Long, added: Boolean, collectionCount: Int) {
        val eventName = if(added) E_USER_ADDED_BOTTLE_TO_COLLECTION else E_USER_REMOVED_BOTTLE_TO_COLLECTION

        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)
        bundle.putInt(F_COLLECTION_SIZE, collectionCount)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(eventName, bundle)
    }

    fun userUpdatedWishList(bottleId: Long, added: Boolean, wishlistCount: Int) {
        val eventName = if(added) E_USER_ADDED_BOTTLE_TO_WISHLIST else E_USER_REMOVED_BOTTLE_TO_WISHLIST

        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)
        bundle.putInt(F_WISHLIST_SIZE, wishlistCount)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(eventName, bundle)
    }

    fun scanBarcodeDialogDisplayed() {
        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SCAN_BARCODE_DIALOG_DISPLAYED, null)
    }

    fun scanBarcodeDialogUserAction(bottleId: Long, agreed: Boolean) {
        val eventName = if(agreed) E_SCAN_BARCODE_USER_AGREED else E_SCAN_BARCODE_USER_DECLINED

        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(eventName, bundle)
    }

    fun userScanedBarcode(bottleId: Long, barcode: String) {
        val bundle = Bundle()
        bundle.putLong(F_BOTTLE_ID, bottleId)
        bundle.putString(F_BARCODE, barcode)

        FirebaseAnalytics.getInstance(SpiritsThiefApplication.get()).logEvent(E_SCAN_BARCODE_USER_UPDATED_BARCODE, bundle)
    }
}