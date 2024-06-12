package com.example.myview.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityTouchViewBinding

class TouchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTouchViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouchViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.touchView.setOnClickListener {
            Log.i(TAG, "OnClickListener")
        }
        binding.touchView.setOnTouchListener { v, event ->
            Log.i(TAG, "OnTouchListener event:$event")
            false
        }
    }

    companion object {
        private const val TAG = "TouchActivity"
    }
}