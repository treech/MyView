package com.example.myview.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

class CustomDragView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var mFrontChild: View
    lateinit var mBackChild: View

    private var mFrontChildHeight = 0

    private var mTop = 0

    private var mDownY = 0f

    private var mIsMenuOpen = false

    private var mVhCb = object : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == mBackChild
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.i(TAG, "top:$top,dy:$dy")
            mTop = top
            return if (top > mFrontChildHeight) mFrontChildHeight else if (top < 0) 0 else top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            Log.i(TAG, "yvel:$yvel")
            if (releasedChild == mBackChild) {
                mIsMenuOpen = mTop > mFrontChildHeight / 3
                mVH.settleCapturedViewAt(0, if (mIsMenuOpen) mFrontChildHeight else 0)
                invalidate()
            }
        }
    }

    override fun computeScroll() {
        if (mVH.continueSettling(true)) {
            invalidate()
        }
    }

    private var mVH: ViewDragHelper = ViewDragHelper.create(this, mVhCb)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 2) {
            throw RuntimeException("only allow two direct children")
        }

        mFrontChild = getChildAt(0)
        mBackChild = getChildAt(1)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mFrontChildHeight = mFrontChild.measuredHeight
            Log.i(TAG, "frontChildHeight:$mFrontChildHeight")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mVH.processTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mIsMenuOpen) {
            return true
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mVH.processTouchEvent(ev)
                mDownY = ev.y
            }

            MotionEvent.ACTION_MOVE -> {
                val diffY = ev.y - mDownY
                Log.d(TAG, "diff:$diffY")
                if (diffY > 0 && !canScrollUp()) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun canScrollUp(): Boolean {
        var result = false
        if (mBackChild is AbsListView) {
            val listView = (mBackChild as AbsListView)
            result = listView.childCount > 0 && listView.firstVisiblePosition > 0
        }
        val result2 = mBackChild.canScrollVertically(-1)
        Log.i(TAG, "result:$result,result2:$result2")
        return result2 || result
    }

    companion object {
        private const val TAG = "CustomDragView"
    }
}