package com.example.myview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityRotateTvBinding

class RotateTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRotateTvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRotateTvBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}