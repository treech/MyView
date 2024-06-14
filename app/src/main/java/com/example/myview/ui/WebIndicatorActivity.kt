package com.example.myview.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityWebIndicatorBinding

class WebIndicatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebIndicatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebIndicatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = 4000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener(mAnimatorUpdateListener)

        binding.loadInit.setOnClickListener {
            binding.indicator.showForTest()
        }
        binding.loadSuccess.setOnClickListener {
            valueAnimator.start()
        }
    }

    private val mAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val t = animation.animatedValue as Float
        binding.indicator.setProgress(t)
    }

    companion object {
        private const val TAG = "WebIndicatorActivity"
    }
}
