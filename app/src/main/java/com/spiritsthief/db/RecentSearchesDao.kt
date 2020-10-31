package com.spiritsthief.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



/**
 * Created by dorayalon on 22/01/2018.
 */
@Dao
interface RecentSearchesDao {
    @Query(
            "SELECT * " +
                    "FROM recent_search " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT :top")
    fun getTop(top: Int): List<RecentSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentSearch: RecentSearch)
}