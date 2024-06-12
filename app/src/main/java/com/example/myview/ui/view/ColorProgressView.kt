package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myview.R
import kotlin.math.min

class ColorProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var mInnerColor: Int
    private var mOuterColor: Int
    private var mRoundSize: Int
    private var mTextSize: Int
    private var mTextColor: Int
    private var mInnerPaint: Paint
    private var mOuterPaint: Paint
    private var mTextPaint: Paint
    var progress = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ColorProgressView)
        mInnerColor = ta.getColor(R.styleable.ColorProgressView_cpv_inner_color, ContextCompat.getColor(context, R.color.blue))
        mOuterColor = ta.getColor(R.styleable.ColorProgressView_cpv_outer_color, ContextCompat.getColor(context, R.color.red))
        mRoundSize = ta.getDimension(R.styleable.ColorProgressView_cpv_round_size, dp2Px(10f)).toInt()
        mTextSize = ta.getDimensionPixelSize(R.styleable.ColorProgressView_cpv_text_size, sp2Px(40f))
        mTextColor = ta.getColor(R.styleable.ColorProgressView_cpv_text_color, ContextCompat.getColor(context, R.color.black))
        mInnerPaint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mRoundSize.toFloat()
            color = mInnerColor
        }
        mOuterPaint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mRoundSize.toFloat()
            color = mOuterColor
        }
        mTextPaint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            textSize = mTextSize.toFloat()
            color = mTextColor
        }
        ta.recycle()
    }

    private fun dp2Px(dip: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics)
    }

    private fun sp2Px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val minSize = min(measureWidth, measureHeight)
        setMeasuredDimension(minSize, minSize)
    }

    override fun onDraw(canvas: Canvas) {
        val circle = width * 0.5f
        canvas.drawCircle(circle, circle, circle - mRoundSize * 0.5f, mInnerPaint)
        val rectF = RectF(mRoundSize * 0.5f, mRoundSize * 0.5f, width.toFloat() - mRoundSize * 0.5f, height.toFloat() - mRoundSize * 0.5f)
        canvas.drawArc(rectF, -90f, 360f * progress, false, mOuterPaint)

        val text = "${(progress * 100).toInt()}%"
        val bounds = Rect()
        mTextPaint.getTextBounds(text, 0, text.length, bounds)
        val startX = (width - bounds.width()) * 0.5f
        val fontMetricsInt = mTextPaint.fontMetricsInt
        val dy = (fontMetricsInt.bottom - fontMetricsInt.top) * 0.5f - fontMetricsInt.bottom
        val startY = height * 0.5f + dy
        canvas.drawText(text, startX, startY, mTextPaint)
    }
}