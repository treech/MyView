package com.example.myview.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MyFlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    private var mLine: Line? = null
    private val mLines = mutableListOf<Line>()

    private var mHorizontalSpacing = 20
    private var mVerticalSpacing = 20

    private var mUsedWith = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val extraWidthSize = widthSpecSize - paddingLeft - paddingRight
        val extraHeightSize = heightSpecSize - paddingTop - paddingBottom

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        restoreLines()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val childWidthSpecMode = if (widthSpecMode == MeasureSpec.EXACTLY) MeasureSpec.AT_MOST else widthSpecMode
            val childHeightSpecMode = if (heightSpecMode == MeasureSpec.EXACTLY) MeasureSpec.AT_MOST else heightSpecMode

            child.measure(MeasureSpec.makeMeasureSpec(extraWidthSize, childWidthSpecMode), MeasureSpec.makeMeasureSpec(extraHeightSize, childHeightSpecMode))
            val childMeasuredWidth = child.measuredWidth
            if (mLine == null) {
                mLine = Line()
            }
            mUsedWith += childMeasuredWidth
            Log.i("ygq", "index:$i,childMeasuredWidth:$childMeasuredWidth,childMeasuredHeight:${child.measuredHeight},mUsedWith:$mUsedWith,extraWidthSize:$extraWidthSize,line size:${mLines.size}")
            if (mUsedWith <= extraWidthSize) {
                mLine?.addView(child)
                mUsedWith += mHorizontalSpacing
                if (mUsedWith >= extraWidthSize) {
                    newLine()
                }
            } else {
                newLine()
                mLine?.addView(child)
                mUsedWith += childMeasuredWidth + mHorizontalSpacing
            }
        }
        //第一行和最后一行未占满一行的情况
        if (mLine != null && mLine!!.getViews().size > 0 && !mLines.contains(mLine)) {
            mLine?.let { mLines.add(it) }
        }

        var totalHeight = 0
        val lineSize = mLines.size
        for (index in 0 until lineSize) {
            totalHeight += mLines[index].mHeight
        }
        totalHeight += paddingTop + paddingBottom + (lineSize - 1) * mVerticalSpacing
        setMeasuredDimension(widthSpecSize, resolveSize(totalHeight, heightMeasureSpec))
    }

    private fun restoreLines() {
        Log.i("ygq", "restoreLines")
        mLines.clear()
        mLine = Line()
        mUsedWith = 0
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            val left = paddingLeft
            var top = paddingTop
            Log.i("ygq", "line size:${mLines.size}")
            mLines.forEach {
                it.onLayout(left, top)
                top += it.mHeight + mVerticalSpacing
            }
        }
    }

    private fun newLine() {
        mLine?.let { mLines.add(it) }
        mLine = Line()
        mUsedWith = 0
    }

    private inner class Line {
        val mViews = mutableListOf<View>()
        var mHeight = 0
        var mWidth = 0

        fun addView(view: View) {
            mViews.add(view)
            mWidth += view.measuredWidth
            mHeight = Math.max(mHeight, view.measuredHeight)
            Log.i("ygq","add view max height:$mHeight,measuredHeight:${view.measuredHeight}")
        }

        fun getViews() = mViews

        fun onLayout(l: Int, t: Int) {
            var left = l
            Log.i("ygq", "views size:${mViews.size},l:$l,t:$t")
            for ((index, view) in mViews.withIndex()) {
                val childWith = view.measuredWidth
                val childHeight = view.measuredHeight
                Log.i("ygq", "index:$index,l:$left,t:$t,r:${left + childWith},b:${t + childHeight}")
                view.layout(left, t, left + childWith, t + childHeight)
                left += childWith + if (index == mViews.size - 1) 0 else mHorizontalSpacing
            }
        }
    }

    fun setAdapter(list: List<String>, layoutResId: Int, snapShot: SnapShot<String>) {
        removeAllViews()
        for ((index, s) in list.withIndex()) {
            val viewHolder = ViewHolder(layoutResId)
            snapShot.getHolderView(s, viewHolder, index)
            viewHolder.addView()
        }
    }

    abstract class SnapShot<T> {
        abstract fun getHolderView(item: T, holder: ViewHolder, position: Int)
    }

    inner class ViewHolder(layoutResId: Int) {

        private var mView: View = LayoutInflater.from(context).inflate(layoutResId, null)
        private var mViews: SparseArray<View> = SparseArray()

        fun setText(viewId: Int, content: String) {
            getView<TextView>(viewId)?.apply {
                text = content
            }
        }

        private fun <T : View> getView(viewId: Int): T? {
            var view = mViews.get(viewId)
            if (view == null) {
                view = mView.findViewById(viewId)
                mViews.put(viewId, view)
                Log.i("ygq", "viewId:$viewId")
            }
            return try {
                view as T
            } catch (e: ClassCastException) {
                e.printStackTrace()
                null
            }
        }

        fun addView() {
            addView(mView)
        }
    }
}