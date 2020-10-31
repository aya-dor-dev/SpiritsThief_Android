package com.spiritsthief.common

/**
 * Created by Dor Ayalon on 12/19/17.
 */
interface SimpleScrollListener {
    fun onScrollUp()
    fun onScrollDown()
    fun scrollTo(x: Int, y: Int)
}