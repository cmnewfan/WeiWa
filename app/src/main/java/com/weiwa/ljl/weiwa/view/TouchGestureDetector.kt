package com.weiwa.ljl.weiwa.view

/**
 * Created by hzfd on 2017/4/12.
 */

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ScaleGestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class TouchGestureDetector(context: Context, listener: OnTouchGestureListener) {

    private val mGestureDetector: GestureDetectorCompat = GestureDetectorCompat(context, listener)
    private val mScaleGestureDetector: ScaleGestureDetector

    init {
        mGestureDetector.setOnDoubleTapListener(listener)
        mScaleGestureDetector = ScaleGestureDetector(context, listener)
        ScaleGestureDetectorCompat.setQuickScaleEnabled(mScaleGestureDetector, false)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        var ret = mScaleGestureDetector.onTouchEvent(event)
        if (!mScaleGestureDetector.isInProgress) {
            ret = ret or mGestureDetector.onTouchEvent(event)
        }
        return ret
    }

    abstract class OnTouchGestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {}

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {}

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return false
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return false
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {}
    }
}
