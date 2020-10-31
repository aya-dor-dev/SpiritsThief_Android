package com.spiritsthief.common

import android.animation.Animator
import android.text.Editable
import android.text.TextWatcher
import android.transition.Transition

open class SimpleAnimationListener: Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}

open class SimpleTextWatcher: TextWatcher {
    override fun afterTextChanged(p0: Editable) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

}

open class SimpleTransitionListener: Transition.TransitionListener {
    override fun onTransitionEnd(p0: Transition?) {
    }

    override fun onTransitionResume(p0: Transition?) {
    }

    override fun onTransitionPause(p0: Transition?) {
    }

    override fun onTransitionCancel(p0: Transition?) {
    }

    override fun onTransitionStart(p0: Transition?) {
    }

}