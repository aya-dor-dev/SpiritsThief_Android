package com.spiritsthief.common

import com.google.android.material.appbar.AppBarLayout

/**
 * Created by Dor Ayalon on 1/11/18.
 */
abstract class CollapsingAppBarListener: AppBarLayout.OnOffsetChangedListener{
    enum class State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private var state = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        when {
            verticalOffset == 0 -> {
                if (state != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                state = State.EXPANDED
            }
            Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                if (state != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                state = State.COLLAPSED
            }
            else -> {
                if (state != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                state = State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State)
}