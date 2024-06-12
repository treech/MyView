package com.example.myview.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myview.databinding.ActivityHomeBinding

class HomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btToMain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btToSliderBar.setOnClickListener {
            startActivity(Intent(this, SliderViewActivity::class.java))
        }
        binding.btToTagLayout.setOnClickListener {
            startActivity(Intent(this, TagLayoutActivity::class.java))
        }
        binding.btToTouchView.setOnClickListener {
            startActivity(Intent(this, TouchActivity::class.java))
        }
        binding.btToScrollView.setOnClickListener {
            startActivity(Intent(this, ScrollActivity::class.java))
        }
    }
}