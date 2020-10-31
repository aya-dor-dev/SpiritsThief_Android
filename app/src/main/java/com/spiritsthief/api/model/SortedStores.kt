package com.spiritsthief.api.model

import com.spiritsthief.common.VERIFIED_THRESHOLD_DAYS

class SortedStores(stores: List<Store>) {
    val verified: MutableList<Store> = mutableListOf()
    val unverified: MutableList<Store> = mutableListOf()
    val soldout: MutableList<Store> = mutableListOf()

    init {
        stores.forEach {
            if (it.valid == 0) {
                soldout.add(it)
            } else if (it.lastUpdate != null) {
                val lu = it.lastUpdate.toInt()
                if (lu <= VERIFIED_THRESHOLD_DAYS) {
                    verified.add(it)
                } else {
                    unverified.add(it)
                }
            } else {
                unverified.add(it)
            }
        }
    }

    fun count() =  verified.size + unverified.size + soldout.size
}