package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class RotateTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var mPaintLarge: Paint? = null
    private var mPaintSmall: Paint? = null
    private val largeText = "18"
    private val smallText = "%"
    private val textSizeLarge = 60f // 大号字体大小
    private val textSizeSmall = 30f // 小号字体大小
    private val spacing = 20f // 大号和小号字体之间的间距

    // 初始化方法，设置 Paint 和文字样式
    init {
        mPaintLarge = Paint()
        mPaintLarge!!.color = Color.BLACK
        mPaintLarge!!.textSize = textSizeLarge
        mPaintLarge!!.isAntiAlias = true

        mPaintSmall = Paint()
        mPaintSmall!!.color = Color.BLACK
        mPaintSmall!!.textSize = textSizeSmall
        mPaintSmall!!.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST) {
            val bounds = Rect()
            mPaintLarge!!.getTextBounds(largeText, 0, largeText.length, bounds)
            val bounds2 = Rect()
            mPaintSmall!!.getTextBounds(smallText, 0, smallText.length, bounds2)
            width = (paddingLeft + bounds.width() + spacing + bounds2.width() + paddingRight).toInt()
        }

        var height = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            val bounds = Rect()
            mPaintLarge!!.getTextBounds(largeText, 0, largeText.length, bounds)
            height = paddingTop + bounds.height() + paddingBottom
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 设置旋转的中心点为左下角
        canvas.rotate(-10f, 0f, height.toFloat())

        val fontMetricsInt = mPaintLarge!!.getFontMetricsInt()
        val dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom
        val baseLineY = height / 2 + dy

        val bounds = Rect()
        mPaintLarge!!.getTextBounds(largeText, 0, largeText.length, bounds)
        val bounds2 = Rect()
        mPaintSmall!!.getTextBounds(smallText, 0, smallText.length, bounds2)

        val baseLineX = width / 2 - (bounds.width() + spacing + bounds2.width()) / 2

        // 绘制大号字体
        canvas.drawText(largeText, paddingLeft.toFloat() + baseLineX, baseLineY.toFloat(), mPaintLarge!!)

        // 绘制小号字体，注意设置合适的坐标，保证有间距
        canvas.drawText(smallText, paddingLeft.toFloat() + baseLineX + bounds.width() + spacing, baseLineY.toFloat(), mPaintSmall!!)
    }
}
