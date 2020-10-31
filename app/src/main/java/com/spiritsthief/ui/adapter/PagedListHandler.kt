package com.spiritsthief.ui.adapter

/**
 * Created by dorayalon on 29/01/2018.
 */
interface PagedListHandler{
    fun hasMore(): Boolean
    fun loadMode()
}