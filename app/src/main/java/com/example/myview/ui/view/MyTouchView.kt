package com.example.myview.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MyTouchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent event:${event}")
        return false
    }

//    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        Log.i(TAG, "dispatchTouchEvent event:${event}")
//        return true
//    }

    companion object {
        private const val TAG = "MyTouchView"
    }
}