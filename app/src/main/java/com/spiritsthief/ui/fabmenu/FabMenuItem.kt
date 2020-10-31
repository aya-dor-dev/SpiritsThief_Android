package com.spiritsthief.ui.fabmenu

import androidx.annotation.DrawableRes
import java.io.Serializable

/**
 * Created by Dor Ayalon on 1/4/18.
 */
data class FabMenuItem(
        val id: Int,
        val title: String? = null,
        @DrawableRes
        val icon: Int = 0
) : Serializable