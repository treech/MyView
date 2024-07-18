package com.example.myview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityLockPatternBinding

class LockPatternActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLockPatternBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLockPatternBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

    }

    companion object {
        private const val TAG = "LockPatternActivity"
    }
}