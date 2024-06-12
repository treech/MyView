package com.example.myview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivitySlidingMenuBinding

class SlidingMenuActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySlidingMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySlidingMenuBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

    }
}