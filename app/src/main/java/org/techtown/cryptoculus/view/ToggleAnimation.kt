package org.techtown.cryptoculus.view

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

class ToggleAnimation {
    companion object {
        fun expand(view: View) = view.startAnimation(expandAction(view))

        private fun expandAction(view: View): Animation {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val height = view.measuredHeight

            view.layoutParams.height = 0
            view.visibility = View.VISIBLE

            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    view.layoutParams.height = if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                    else (height * interpolatedTime).toInt()

                    view.requestLayout()
                }
            }

            animation.duration = 500

            view.startAnimation(animation)

            return animation
        }

        fun collapse(view: View) {
            val height = view.measuredHeight

            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    if (interpolatedTime == 1f)
                        view.visibility = View.GONE
                    else {
                        view.apply {
                            layoutParams.height = (height - (height * interpolatedTime)).toInt()
                            requestLayout()
                        }
                    }
                }
            }

            animation.duration = 500

            view.startAnimation(animation)
        }
    }
}