package com.example.myview.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.example.myview.R
import kotlin.math.min

class WebIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 进度条颜色
     */
    @ColorInt
    private var mColor = ResourcesCompat.getColor(resources, R.color.color_1aad19, null)

    /**
     * 进度条的画笔
     */
    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = mColor
        isDither = true
        strokeCap = Paint.Cap.SQUARE
    }

    /**
     * 进度条动画
     */
    private var mAnimator: Animator? = null

    /**
     * 控件的宽度
     */
    private var mTargetWidth = 0

    /**
     * 当前匀速动画最大的时长
     */
    private var mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION

    /**
     * 当前加速后减速动画最大时长
     */
    private var mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION

    /**
     * 结束动画时长
     */
    private var mCurrentDoEndAnimationDuration = DO_END_ANIMATION_DURATION

    /**
     * 当前进度条的状态
     */
    private var indicatorStatus = 0
    private var mCurrentProgress = 0f

    /**
     * 默认的高度
     */
    var mWebIndicatorDefaultHeight: Int = 3

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WebIndicator)
        mColor = ta.getColor(R.styleable.WebIndicator_web_indicator_color, mColor)
        mPaint.color = mColor
        ta.recycle()
        mTargetWidth = context.resources.displayMetrics.widthPixels
        mWebIndicatorDefaultHeight = dp2px(3)
    }

    fun setColor(color: Int) {
        this.mColor = color
        mPaint.color = color
    }

    fun setColor(color: String?) {
        this.setColor(Color.parseColor(color))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var w = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)

        if (wMode == MeasureSpec.AT_MOST) {
            w = min(w.toDouble(), context.resources.displayMetrics.widthPixels.toDouble()).toInt()
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = mWebIndicatorDefaultHeight
        }
        this.setMeasuredDimension(w, h)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, mCurrentProgress / 100 * this.width, this.height.toFloat(), mPaint)
    }

    fun show() {
        if (visibility == GONE) {
            this.visibility = VISIBLE
            mCurrentProgress = 0f
            this.alpha = 1.0f
            startAnim(false)
        }
    }

    fun showForTest() {
        this.visibility = VISIBLE
        mCurrentProgress = 0f
        this.alpha = 1.0f
        startAnim(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.mTargetWidth = measuredWidth
        val screenWidth = context.resources.displayMetrics.widthPixels
        if (mTargetWidth >= screenWidth) {
            mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION
            mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION
            mCurrentDoEndAnimationDuration = MAX_DECELERATE_SPEED_DURATION
        } else {
            //取比值
            val rate = (mTargetWidth / screenWidth).toFloat()
            mCurrentMaxUniformSpeedDuration = (MAX_UNIFORM_SPEED_DURATION * rate).toInt()
            mCurrentMaxDecelerateSpeedDuration = (MAX_DECELERATE_SPEED_DURATION * rate).toInt()
            mCurrentDoEndAnimationDuration = (DO_END_ANIMATION_DURATION * rate).toInt()
        }
    }

    fun setProgress(progress: Float) {
        if (visibility == GONE) {
            visibility = VISIBLE
        }
        if (progress < 95f) {
            return
        }
        if (indicatorStatus != FINISH) {
            startAnim(true)
        }
    }

    fun hide() {
        indicatorStatus = FINISH
    }

    private fun startAnim(isFinished: Boolean) {
        val v = (if (isFinished) 100 else 95).toFloat()
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
        }
        mCurrentProgress = if (mCurrentProgress == 0f) 0.00000001f else mCurrentProgress
        if (!isFinished) {
            AnimatorSet().apply {
                val p1 = v * 0.60f
                val p2 = v
                val animator0 = ValueAnimator.ofFloat(mCurrentProgress, p1)
                val animator1 = ValueAnimator.ofFloat(p1, p2)
                val residue = 1f - mCurrentProgress / 100 - 0.05f
                val duration = (residue * mCurrentMaxUniformSpeedDuration).toLong()
                //前半段40%+后半段60%
                val duration4 = (duration * 0.4f).toLong()
                val duration6 = (duration * 0.6f).toLong()
                animator0.interpolator = LinearInterpolator()
                animator0.setDuration(duration4)
                animator0.addUpdateListener(mAnimatorUpdateListener)

                animator1.interpolator = LinearInterpolator()
                animator1.setDuration(duration6)
                animator1.addUpdateListener(mAnimatorUpdateListener)
                play(animator1).after(animator0)
                start()
                this@WebIndicator.mAnimator = this
            }
        } else {
            var segment95Animator: ValueAnimator? = null
            if (mCurrentProgress < 95f) {
                segment95Animator = ValueAnimator.ofFloat(mCurrentProgress, 95f)
                val residue = 1f - mCurrentProgress / 100f - 0.05f
                segment95Animator.setDuration((residue * mCurrentMaxDecelerateSpeedDuration).toLong())
                segment95Animator.interpolator = DecelerateInterpolator()
                segment95Animator.addUpdateListener(mAnimatorUpdateListener)
            }
            val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
            alphaAnimator.setDuration(mCurrentDoEndAnimationDuration.toLong())
            val valueAnimatorEnd = ValueAnimator.ofFloat(95f, 100f)
            valueAnimatorEnd.setDuration(mCurrentDoEndAnimationDuration.toLong())
            valueAnimatorEnd.addUpdateListener(mAnimatorUpdateListener)
            var animatorSet = AnimatorSet()
            animatorSet.playTogether(alphaAnimator, valueAnimatorEnd)
            if (segment95Animator != null) {
                val animatorSet0 = AnimatorSet()
                animatorSet0.play(animatorSet).after(segment95Animator)
                animatorSet = animatorSet0
            }
            animatorSet.addListener(mAnimatorListenerAdapter)
            animatorSet.start()
            mAnimator = animatorSet
        }
        indicatorStatus = STARTED
    }

    private val mAnimatorUpdateListener = AnimatorUpdateListener { animation ->
        val t = animation.animatedValue as Float
        this@WebIndicator.mCurrentProgress = t
        this@WebIndicator.invalidate()
    }

    private val mAnimatorListenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            doEnd()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        /**
         * animator cause leak , if not cancel;
         */
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
            mAnimator = null
        }
    }

    private fun doEnd() {
        if (indicatorStatus == FINISH && mCurrentProgress == 100f) {
            visibility = GONE
            mCurrentProgress = 0f
            //动画执行结束一定要将透明度还原，否则后面的draw操作看不到东西
            this.alpha = 1f
        }
        indicatorStatus = UN_START
    }

    fun reset() {
        mCurrentProgress = 0f
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
        }
    }

    fun setProgress(newProgress: Int) {
        setProgress(newProgress.toFloat())
    }

    fun dp2px(dip: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    companion object {
        /**
         * 默认匀速动画最大的时长
         */
        const val MAX_UNIFORM_SPEED_DURATION: Int = 8 * 1000

        /**
         * 默认加速后减速动画最大时长
         */
        const val MAX_DECELERATE_SPEED_DURATION: Int = 450

        /**
         * 结束动画时长 ， Fade out 。
         */
        const val DO_END_ANIMATION_DURATION: Int = 600
        const val UN_START: Int = 0
        const val STARTED: Int = 1
        const val FINISH: Int = 2
    }
}
