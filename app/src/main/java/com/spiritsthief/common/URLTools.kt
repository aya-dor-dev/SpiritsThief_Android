package com.spiritsthief.common

import android.net.Uri
import java.net.URLDecoder

/**
 * Created by dorayalon on 23/01/2018.
 */
class URLTools {
    companion object {
        fun splitQuery(url: Uri): Map<String, String> {
            val queryPairs = LinkedHashMap<String, String>()
            val query = url.query
            val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (pair in pairs) {
                val idx = pair.indexOf("=")
                queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            }
            return queryPairs
        }
    }
}