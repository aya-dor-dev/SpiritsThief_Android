package com.spiritsthief.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by dorayalon on 22/01/2018.
 */
@Database(entities = [(RecentSearch::class)], version = 1)
abstract class AppDB: RoomDatabase(){
    abstract fun recentSearchesDao(): RecentSearchesDao
}