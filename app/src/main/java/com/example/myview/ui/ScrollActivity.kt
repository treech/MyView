package com.example.myview.ui

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityScrollviewBinding

class ScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityScrollviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var time = 0L
        var rawY = 0f
        var rawX = 0f
        binding.scrollView2.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        rawX = event.x
                        rawY = event.y
                    }

                    MotionEvent.ACTION_DOWN -> {
                        val diffX = Math.abs(event.x - rawX)
                        val diffY = Math.abs(event.y - rawY)
                        Log.i("ScrollActivity","diffX:$diffX,diffY:$diffY")
                        if (diffX > 100 && diffY < 100) {//横向滑动
                            binding.scrollView2.requestDisallowInterceptTouchEvent(true)
                            return false
                        } else {//竖向滑动
                            binding.scrollView2.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        rawX = 0f
                        rawY = 0f
                        time = 0L
                    }
                }
                return false
            }
        })
    }
}