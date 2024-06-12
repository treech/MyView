package com.example.myview.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import com.example.myview.databinding.ActivitySliderViewBinding
import com.example.myview.ui.view.OnTouchLetterCallback


class SliderViewActivity : ComponentActivity() {

    private lateinit var binding: ActivitySliderViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btBack.setOnClickListener { finish() }
        binding.sliderView.setOnTouchLetterListener(object : OnTouchLetterCallback {
            override fun fingerDown() {
                Log.i("ygq", "fingerDown")
                binding.tvSlide.visibility = View.VISIBLE
            }

            override fun callback(position: Int, letter: String) {
                Log.i("ygq", "position:$position,letter:$letter")
                binding.tvSlide.textContent = letter
            }

            override fun fingerUp() {
                Log.i("ygq", "fingerUp")
                binding.tvSlide.postDelayed({
                    binding.tvSlide.visibility = View.GONE
                }, 1000L)
            }
        })
    }
}