package com.weiwa.ljl.weiwa.view

/**
 * Created by hzfd on 2017/4/12.
 */

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.Toast
import com.diegocarloslima.byakugallery.lib.FlingScroller

class TouchImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    private var mDrawable: Drawable? = null
    private var mDrawableIntrinsicWidth: Int = 0
    private var mDrawableIntrinsicHeight: Int = 0

    private val mTouchGestureDetector: TouchGestureDetector

    private val mMatrix = Matrix()
    private val mMatrixValues = FloatArray(9)

    private var mScale: Float = 0.toFloat()
    private var mMaxScale = 1f
    private var mTranslationX: Float = 0.toFloat()
    private var mTranslationY: Float = 0.toFloat()

    private var mLastFocusX: Float? = null
    private var mLastFocusY: Float? = null

    private val mFlingScroller = FlingScroller()
    private var mIsAnimatingBack: Boolean = false

    init {
        val listener = object : TouchGestureDetector.OnTouchGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return performClick()
            }

            override fun onLongPress(e: MotionEvent) {
                performLongClick()
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                loadMatrixValues()
                val minScale = minScale
                // If we have already zoomed in, we should return to our initial scale value (minScale). Otherwise, scale to full size
                val targetScale = if (mScale > minScale) minScale else mMaxScale
                // First, we try to keep the focused point in the same position when the animation ends
                val desiredTranslationX = e.x - (e.x - mTranslationX) * (targetScale / mScale)
                val desiredTranslationY = e.y - (e.y - mTranslationY) * (targetScale / mScale)
                // Here, we apply a correction to avoid unwanted blank spaces
                val targetTranslationX = desiredTranslationX + computeTranslation(measuredWidth.toFloat(), mDrawableIntrinsicWidth * targetScale, desiredTranslationX, 0f)
                val targetTranslationY = desiredTranslationY + computeTranslation(measuredHeight.toFloat(), mDrawableIntrinsicHeight * targetScale, desiredTranslationY, 0f)
                clearAnimation()
                val animation = TouchAnimation(targetScale, targetTranslationX, targetTranslationY)
                animation.duration = DOUBLE_TAP_ANIMATION_DURATION.toLong()
                startAnimation(animation)
                return true
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                // Sometimes, this method is called just after an onScaleEnd event. In this case, we want to wait until we animate back our image
                if (mIsAnimatingBack) {
                    return false
                }
                loadMatrixValues()
                val currentDrawableWidth = mDrawableIntrinsicWidth * mScale
                val currentDrawableHeight = mDrawableIntrinsicHeight * mScale
                val dx = computeTranslation(measuredWidth.toFloat(), currentDrawableWidth, mTranslationX, -distanceX)
                val dy = computeTranslation(measuredHeight.toFloat(), currentDrawableHeight, mTranslationY, -distanceY)
                mMatrix.postTranslate(dx, dy)
                clearAnimation()
                ViewCompat.postInvalidateOnAnimation(this@TouchImageView)
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                // Sometimes, this method is called just after an onScaleEnd event. In this case, we want to wait until we animate back our image
                if (mIsAnimatingBack) {
                    return false
                }
                loadMatrixValues()
                val horizontalSideFreeSpace = (measuredWidth - mDrawableIntrinsicWidth * mScale) / 2f
                val minTranslationX = if (horizontalSideFreeSpace > 0) horizontalSideFreeSpace else measuredWidth - mDrawableIntrinsicWidth * mScale
                val maxTranslationX = if (horizontalSideFreeSpace > 0) horizontalSideFreeSpace else 0.0f
                val verticalSideFreeSpace = (measuredHeight - mDrawableIntrinsicHeight * mScale) / 2f
                val minTranslationY = if (verticalSideFreeSpace > 0) verticalSideFreeSpace else measuredHeight - mDrawableIntrinsicHeight * mScale
                val maxTranslationY = if (verticalSideFreeSpace > 0) verticalSideFreeSpace else 0.0f
                // Using FlingScroller here. The results were better than the Scroller class
                // https://android.googlesource.com/platform/packages/apps/Gallery2/+/master/src/com/android/gallery3d/ui/FlingScroller.java
                mFlingScroller.fling(Math.round(mTranslationX), Math.round(mTranslationY), Math.round(velocityX), Math.round(velocityY), Math.round(minTranslationX), Math.round(maxTranslationX), Math.round(minTranslationY), Math.round(maxTranslationY))
                clearAnimation()
                val animation = FlingAnimation()
                animation.duration = mFlingScroller.duration.toLong()
                animation.interpolator = LinearInterpolator()
                startAnimation(animation)
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                mLastFocusX = null
                mLastFocusY = null
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                try {
                    loadMatrixValues()
                    val currentDrawableWidth = mDrawableIntrinsicWidth * mScale
                    val currentDrawableHeight = mDrawableIntrinsicHeight * mScale
                    val focusX = computeFocus(measuredWidth.toFloat(), currentDrawableWidth, mTranslationX, detector.focusX)
                    val focusY = computeFocus(measuredHeight.toFloat(), currentDrawableHeight, mTranslationY, detector.focusY)
                    // Here, we provide the ability to scroll while scaling
                    if (mLastFocusX != null && mLastFocusY != null) {
                        val dx = computeScaleTranslation(measuredWidth.toFloat(), currentDrawableWidth, mTranslationX, focusX - mLastFocusX!!)
                        val dy = computeScaleTranslation(measuredHeight.toFloat(), currentDrawableHeight, mTranslationY, focusY - mLastFocusY!!)
                        if (dx != 0f || dy != 0f) {
                            mMatrix.postTranslate(dx, dy)
                        }
                    }
                    val scale = computeScale(minScale, mMaxScale, mScale, detector.scaleFactor)
                    mMatrix.postScale(scale, scale, focusX, focusY)
                    mLastFocusX = focusX
                    mLastFocusY = focusY
                    clearAnimation()
                    ViewCompat.postInvalidateOnAnimation(this@TouchImageView)
                } catch (ex: Exception) {
                    Toast.makeText(getContext(), ex.message, Toast.LENGTH_SHORT).show()
                }

                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                try {
                    loadMatrixValues()
                    val currentDrawableWidth = mDrawableIntrinsicWidth * mScale
                    val currentDrawableHeight = mDrawableIntrinsicHeight * mScale
                    val dx = computeTranslation(measuredWidth.toFloat(), currentDrawableWidth, mTranslationX, 0f)
                    val dy = computeTranslation(measuredHeight.toFloat(), currentDrawableHeight, mTranslationY, 0f)
                    if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                        return
                    }
                    val targetTranslationX = mTranslationX + dx
                    val targetTranslationY = mTranslationY + dy
                    clearAnimation()
                    val animation = TouchAnimation(mScale, targetTranslationX, targetTranslationY)
                    animation.duration = SCALE_END_ANIMATION_DURATION.toLong()
                    startAnimation(animation)
                    mIsAnimatingBack = true
                } catch (ex: Exception) {
                    Toast.makeText(getContext(), ex.message, Toast.LENGTH_SHORT).show()
                }

            }
        }

        mTouchGestureDetector = TouchGestureDetector(context, listener)

        super.setScaleType(ImageView.ScaleType.MATRIX)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val oldMeasuredWidth = measuredWidth
        val oldMeasuredHeight = measuredHeight

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (oldMeasuredWidth != measuredWidth || oldMeasuredHeight != measuredHeight) {
            resetToInitialState()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.setImageMatrix(mMatrix)
        super.onDraw(canvas)
    }

    override fun setImageMatrix(matrix: Matrix?) {
        var matrix = matrix
        if (matrix == null) {
            matrix = Matrix()
        }

        if (mMatrix != matrix) {
            mMatrix.set(matrix)
            invalidate()
        }
    }

    override fun getImageMatrix(): Matrix {
        return mMatrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            mTouchGestureDetector.onTouchEvent(event)
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        }

        return true
    }

    override fun clearAnimation() {
        super.clearAnimation()
        mIsAnimatingBack = false
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (mDrawable !== drawable) {
            mDrawable = drawable
            if (drawable != null) {
                mDrawableIntrinsicWidth = drawable.intrinsicWidth
                mDrawableIntrinsicHeight = drawable.intrinsicHeight
                resetToInitialState()
            } else {
                mDrawableIntrinsicWidth = 0
                mDrawableIntrinsicHeight = 0
            }
        }
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType != ImageView.ScaleType.MATRIX) {
            throw IllegalArgumentException("Unsupported scaleType. Only ScaleType.MATRIX is allowed.")
        }
        super.setScaleType(scaleType)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        loadMatrixValues()
        return canScroll(measuredWidth.toFloat(), mDrawableIntrinsicWidth * mScale, mTranslationX, direction)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun canScrollVertically(direction: Int): Boolean {
        loadMatrixValues()
        return canScroll(measuredHeight.toFloat(), mDrawableIntrinsicHeight * mScale, mTranslationY, direction)
    }

    fun setMaxScale(maxScale: Float) {
        mMaxScale = maxScale
    }

    fun performDoubleTap() {
        loadMatrixValues()
        val minScale = minScale
        // If we have already zoomed in, we should return to our initial scale value (minScale). Otherwise, scale to full size
        val targetScale = if (mScale > minScale) minScale else mMaxScale
        // First, we try to keep the focused point in the same position when the animation ends
        val displayMetrics = resources.displayMetrics
        val width = 0
        val height = 0
        val desiredTranslationX = width - (width - mTranslationX) * (targetScale / mScale)
        val desiredTranslationY = height - (height - mTranslationY) * (targetScale / mScale)
        // Here, we apply a correction to avoid unwanted blank spaces
        val targetTranslationX = 0f
        val targetTranslationY = 0f
        clearAnimation()
        val animation = TouchAnimation(targetScale, targetTranslationX, targetTranslationY)
        animation.duration = DOUBLE_TAP_ANIMATION_DURATION.toLong()
        startAnimation(animation)
    }

    private fun resetToInitialState() {
        mMatrix.reset()
        val minScale = minScale
        mMatrix.postScale(minScale, minScale)

        val values = FloatArray(9)
        mMatrix.getValues(values)

        val freeSpaceHorizontal = (measuredWidth - mDrawableIntrinsicWidth * minScale) / 2f
        val freeSpaceVertical = (measuredHeight - mDrawableIntrinsicHeight * minScale) / 2f
        mMatrix.postTranslate(freeSpaceHorizontal, freeSpaceVertical)

        invalidate()
    }

    private fun loadMatrixValues() {
        mMatrix.getValues(mMatrixValues)
        mScale = mMatrixValues[Matrix.MSCALE_X]
        mTranslationX = mMatrixValues[Matrix.MTRANS_X]
        mTranslationY = mMatrixValues[Matrix.MTRANS_Y]
    }

    private val minScale: Float
        get() {
            var minScale = Math.min(measuredWidth / mDrawableIntrinsicWidth.toFloat(), measuredHeight / mDrawableIntrinsicHeight.toFloat())
            if (minScale > mMaxScale) {
                minScale = mMaxScale
            }
            return minScale
        }

    private inner class FlingAnimation : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            mFlingScroller.computeScrollOffset(interpolatedTime)

            loadMatrixValues()

            val dx = mFlingScroller.currX - mTranslationX
            val dy = mFlingScroller.currY - mTranslationY
            mMatrix.postTranslate(dx, dy)

            ViewCompat.postInvalidateOnAnimation(this@TouchImageView)
        }
    }

    private inner class TouchAnimation internal constructor(private val targetScale: Float, private val targetTranslationX: Float, private val targetTranslationY: Float) : Animation() {

        private val initialScale: Float
        private val initialTranslationX: Float
        private val initialTranslationY: Float

        init {
            loadMatrixValues()

            this.initialScale = mScale
            this.initialTranslationX = mTranslationX
            this.initialTranslationY = mTranslationY
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            loadMatrixValues()

            if (interpolatedTime >= 1) {
                mMatrix.getValues(mMatrixValues)
                mMatrixValues[Matrix.MSCALE_X] = this.targetScale
                mMatrixValues[Matrix.MSCALE_Y] = this.targetScale
                mMatrixValues[Matrix.MTRANS_X] = this.targetTranslationX
                mMatrixValues[Matrix.MTRANS_Y] = this.targetTranslationY
                mMatrix.setValues(mMatrixValues)

            } else {
                val scaleFactor = (this.initialScale + interpolatedTime * (this.targetScale - this.initialScale)) / mScale
                mMatrix.postScale(scaleFactor, scaleFactor)

                mMatrix.getValues(mMatrixValues)
                val currentTranslationX = mMatrixValues[Matrix.MTRANS_X]
                val currentTranslationY = mMatrixValues[Matrix.MTRANS_Y]

                val dx = this.initialTranslationX + interpolatedTime * (this.targetTranslationX - this.initialTranslationX) - currentTranslationX
                val dy = this.initialTranslationY + interpolatedTime * (this.targetTranslationY - this.initialTranslationY) - currentTranslationY
                mMatrix.postTranslate(dx, dy)
            }

            ViewCompat.postInvalidateOnAnimation(this@TouchImageView)
        }
    }

    companion object {

        private val DOUBLE_TAP_ANIMATION_DURATION = 300
        private val SCALE_END_ANIMATION_DURATION = 200

        private fun canScroll(viewSize: Float, drawableSize: Float, currentTranslation: Float, direction: Int): Boolean {
            if (direction > 0) {
                return Math.round(currentTranslation) < 0
            } else if (direction < 0) {
                return Math.round(currentTranslation) > viewSize - Math.round(drawableSize)
            }
            return false
        }

        // The translation values must be in [0, viewSize - drawableSize], except if we have free space. In that case we will translate to half of the free space
        private fun computeTranslation(viewSize: Float, drawableSize: Float, currentTranslation: Float, delta: Float): Float {
            val sideFreeSpace = (viewSize - drawableSize) / 2f

            if (sideFreeSpace > 0) {
                return sideFreeSpace - currentTranslation
            } else if (currentTranslation + delta > 0) {
                return -currentTranslation
            } else if (currentTranslation + delta < viewSize - drawableSize) {
                return viewSize - drawableSize - currentTranslation
            }

            return delta
        }

        private fun computeScaleTranslation(viewSize: Float, drawableSize: Float, currentTranslation: Float, delta: Float): Float {
            val minTranslation = if (viewSize > drawableSize) 0.0f else viewSize - drawableSize
            val maxTranslation = if (viewSize > drawableSize) viewSize - drawableSize else 0.0f

            if (currentTranslation < minTranslation && delta > 0) {
                if (currentTranslation + delta > maxTranslation) {
                    return maxTranslation - currentTranslation
                } else {
                    return delta
                }
            } else if (currentTranslation > maxTranslation && delta < 0) {
                if (currentTranslation + delta < minTranslation) {
                    return minTranslation - currentTranslation
                } else {
                    return delta
                }
            } else if (currentTranslation > minTranslation && currentTranslation < maxTranslation) {
                if (currentTranslation + delta < minTranslation) {
                    return minTranslation - currentTranslation
                } else if (currentTranslation + delta > maxTranslation) {
                    return maxTranslation - currentTranslation
                } else {
                    return delta
                }
            }
            return 0f
        }

        // If our focal point is outside the image, we will project it to our image bounds
        private fun computeFocus(viewSize: Float, drawableSize: Float, currentTranslation: Float, focusCoordinate: Float): Float {
            if (currentTranslation > 0 && focusCoordinate < currentTranslation) {
                return currentTranslation
            } else if (currentTranslation < viewSize - drawableSize && focusCoordinate > currentTranslation + drawableSize) {
                return drawableSize + currentTranslation
            }

            return focusCoordinate
        }

        // The scale values must be in [minScale, maxScale]
        private fun computeScale(minScale: Float, maxScale: Float, currentScale: Float, delta: Float): Float {
            if (currentScale * delta < minScale) {
                return minScale / currentScale
            } else if (currentScale * delta > maxScale) {
                return maxScale / currentScale
            }

            return delta
        }
    }
}
