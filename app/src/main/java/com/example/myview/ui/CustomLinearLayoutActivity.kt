package com.example.myview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityCustomLinearlayoutBinding

class CustomLinearLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLinearlayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomLinearlayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        private const val TAG = "CustomLinearLayoutActivity"
    }
}