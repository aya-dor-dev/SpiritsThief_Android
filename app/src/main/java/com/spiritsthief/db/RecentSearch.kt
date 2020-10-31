package com.spiritsthief.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by dorayalon on 22/01/2018.
 */
@Entity(tableName = "recent_search",
        indices = arrayOf(Index(name = "query_idx", value = arrayOf("query"), unique = true)))
 data class RecentSearch(
        @ColumnInfo(name = "timestamp")
        var timeStamp: Long,
        @ColumnInfo(name = "query",index = true)
        var query: String,
        @ColumnInfo(name = "type")
        var type: Int,
        @ColumnInfo(name = "category")
        var category: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}