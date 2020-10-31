package com.spiritsthief

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.db.AppDB
import com.spiritsthief.repository.*

/**
 * Created by Dor Ayalon on 1/9/18.
 */
class SpiritsThiefApplication: Application() {
    companion object {
        private lateinit var APP: SpiritsThiefApplication

        fun get() = APP
    }
    override fun onCreate() {
        super.onCreate()
        APP = this
    }
}