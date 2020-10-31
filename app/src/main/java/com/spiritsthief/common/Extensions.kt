package com.spiritsthief.common

import android.animation.Animator
import android.content.res.Resources
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import com.spiritsthief.R

public data class Quadrouple<out A, out B, out C, out D>(
        public val first: A,
        public val second: B,
        public val third: C,
        public val fourth: D?
)

fun View.rotateForever() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely))
}

fun View.stopRotating() {
    animation?.cancel()
}

fun View.show(duration: Long = 150) {
//    animation?.cancel()
//    animation?.setAnimationListener(null)
    visibility = VISIBLE
//    post {
//        animate().setDuration(duration).alpha(1f).start()
//    }
}

fun View.hide(duration: Long = 150) {
//    animation?.cancel()
//    animation?.setAnimationListener(null)
//    animate().setDuration(duration).alpha(0f).setListener(object : SimpleAnimationListener() {
//        override fun onAnimationEnd(animation: Animator?) {
//            this@hide.animation?.setAnimationListener(null)
            visibility = GONE
//        }
//    }).start()
}

fun List<String>.toStringEllipsize(res: Resources): String {
    var str = ""

    if (!isEmpty()) {
        if (size > 1) {
            str += this[0] + " "
            str += String.format(res.getString(R.string.filter_value_more), size - 1)
        } else {
            str = this[0]
        }
    }

    return str
}

fun List<Any>.sameContent(other: List<Any>?): Boolean = other != null && other.containsAll(this) && this.containsAll(other)