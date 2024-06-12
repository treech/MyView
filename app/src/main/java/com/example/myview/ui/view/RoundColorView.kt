package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.example.myview.R
import kotlin.math.min

class RoundColorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var mTextSize: Int = sp2Px(14f)
    private var mCircleWidth = dp2Px(30f)
    private var mBgColor = Color.RED
    private var mTextColor = Color.BLACK
    private var mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    var textContent = ""
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundColorView)
        mTextSize = ta.getDimensionPixelSize(R.styleable.RoundColorView_rcv_text_size, sp2Px(14f))
        mTextColor = ta.getColor(R.styleable.RoundColorView_rcv_text_color, Color.BLACK)
        mBgColor = ta.getColor(R.styleable.RoundColorView_rcv_bg_color, Color.RED)
        mTextPaint.apply {
            textSize = mTextSize.toFloat()
            color = mTextColor
        }
        mBgPaint.color = mBgColor
        ta.recycle()
    }

    private fun sp2Px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()
    }

    private fun dp2Px(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val minSize = min(min(measureWidth, measureHeight), mCircleWidth)
        setMeasuredDimension(minSize, minSize)
    }

    override fun onDraw(canvas: Canvas) {
        val circle = width * 0.5f
        canvas.drawCircle(circle, circle, circle, mBgPaint)
        //measureText实际上是字体整体宽度，即左右带有一定的宽度
        //getTextBounds获得的是字符串的最小矩形区域
        //所以在计算字体宽度的时候，通常使用measureText计算字体所占的宽度
        val textWidth = mTextPaint.measureText(textContent)
        val startX = width * 0.5f - textWidth * 0.5f
        val fontMetricsInt = mTextPaint.fontMetricsInt
        val dy = (fontMetricsInt.bottom - fontMetricsInt.top) * 0.5f - fontMetricsInt.bottom
        val baseLineY = height * 0.5f + dy
        canvas.drawText(textContent, startX, baseLineY, mTextPaint)
    }
}