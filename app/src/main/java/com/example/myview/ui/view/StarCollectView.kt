package com.example.myview.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.myview.R

class StarCollectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var defaultStarImage: Bitmap
    private var selectStarImage: Bitmap
    private var starCount = 0
    private var selectIndex = -1
    private var cacheIndex = -1

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StarCollectView)
        val defaultStarResId = ta.getResourceId(R.styleable.StarCollectView_scv_star_normal, R.drawable.ic_collect_default)
        val selectStarResId = ta.getResourceId(R.styleable.StarCollectView_scv_star_select, R.drawable.ic_collect_select)
        defaultStarImage = BitmapFactory.decodeResource(resources, defaultStarResId)
        selectStarImage = BitmapFactory.decodeResource(resources, selectStarResId)
        starCount = ta.getInt(R.styleable.StarCollectView_scv_star_count, 0)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(defaultStarImage.width * starCount, defaultStarImage.height)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0..starCount) {
            canvas.drawBitmap(if (i < selectIndex) selectStarImage else defaultStarImage, i * defaultStarImage.width.toFloat(), 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e("ygq", "onTouchEvent:$event")
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                selectIndex = (event.x / defaultStarImage.width + 1).toInt()
                Log.e("ygq", "selectIndex:${event.x}")
                if (event.x < 0) {
                    selectIndex = -1
                } else if (event.x > defaultStarImage.width * starCount) {
                    selectIndex = starCount
                }
                if (selectIndex == cacheIndex) {
                    return true
                }
                invalidate()
                cacheIndex = selectIndex
            }
        }
        return true
    }
}