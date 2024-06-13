package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

class CustomLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

//    init {
//        setWillNotDraw(false)
//    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.i(TAG,"onFinishInflate")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i(TAG,"onMeasure")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.i(TAG,"onLayout changed:$changed")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG,"onSizeChanged")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i(TAG,"onDraw")
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        Log.i(TAG,"onWindowFocusChanged hasWindowFocus:$hasWindowFocus")
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        Log.i(TAG,"onWindowVisibilityChanged visibility:$visibility")
    }

    companion object{
        private const val TAG = "CustomLinearLayout"
    }
}