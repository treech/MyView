package com.example.myview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityHomeIndicatorBinding

class HomeIndicatorActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityHomeIndicatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeIndicatorBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.root.setWindow(window)
    }
}