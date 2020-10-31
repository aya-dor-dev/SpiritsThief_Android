package com.spiritsthief.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.spiritsthief.SpiritsThiefApplication
import com.spiritsthief.common.AnalyticsHelper

object UserCollections {
    private const val USER_COLLECTIONS = "user_collections"
    private const val WISH_LIST = "wish_list"
    private const val COLLECTION = "collection"

    val wishList = MutableLiveData<List<Long>>()
    val collection = MutableLiveData<List<Long>>()

    init {
        wishList.value = loadList(WISH_LIST).map { it.toLong() }
        collection.value = loadList(COLLECTION).map { it.toLong() }
    }

    fun isInWishList(id: Long) = loadList(WISH_LIST).contains(id.toString())

    /**
     * @return true if added, false if removed
     */
    fun addOrRemoveToWishList(id: Long): Boolean {
        val id = id.toString()
        val list = loadList(WISH_LIST)
        val added = if (list.contains(id)) {
            list.remove(id)
            false
        } else {
            list.add(id)
            true
        }

        saveList(WISH_LIST, list)
        wishList.postValue(list.map { it.toLong() })
        AnalyticsHelper.userUpdatedWishList(id.toLong(), added, list.count())
        return added
    }

    fun isInCollection(id: Long) = loadList(COLLECTION).contains(id.toString())

    /**
     * @return true if added, false if removed
     */
    fun addOrRemoveToCollection(id: Long): Boolean {
        val id = id.toString()
        val list = loadList(COLLECTION)
        val added = if (list.contains(id)) {
            list.remove(id)
            false
        } else {
            list.add(id)
            true
        }

        saveList(COLLECTION, list)
        collection.postValue(list.map { it.toLong() })
        AnalyticsHelper.userUpdatedCollection(id.toLong(), added, list.count())
        return added
    }

    private fun saveList(collectionName: String, list: MutableSet<String>) {
        var str = list.fold("") { str, next ->
            "$str;$next"
        }

        if (str.isNotEmpty()) str = str.substring(1)

        getSharedPref().edit().putString(collectionName, str).apply()
    }

    private fun loadList(collectionName: String): MutableSet<String> {
        val str = getSharedPref().getString(collectionName, "")!!
        return if (str.isEmpty()) {
            mutableSetOf()
        } else {
            str.split(";").toMutableSet()
        }
    }

    private fun getSharedPref() =
            SpiritsThiefApplication.get().getSharedPreferences(USER_COLLECTIONS, Context.MODE_PRIVATE)
}
