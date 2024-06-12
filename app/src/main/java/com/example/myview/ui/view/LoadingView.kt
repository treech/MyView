package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min

class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var mShape = Shape.SQUARE
        set(value) {
            field = value
            invalidate()
        }

    private var mPaint: Paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val minSize = min(measureWidth, measureHeight)
        setMeasuredDimension(minSize, minSize)
    }

    override fun onDraw(canvas: Canvas) {
        when (mShape) {
            Shape.SQUARE -> {
                mPaint.color = Color.BLUE
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
            }

            Shape.CIRCLE -> {
                mPaint.color = Color.RED
                val circle = width * 0.5f
                canvas.drawCircle(circle, circle, circle, mPaint)
            }

            else -> {
                mPaint.color = Color.GREEN
                val path = Path()
                path.moveTo(width * 0.5f, 0f)
                path.lineTo(0f, (2 * cos(30 * 2 * Math.PI / 360) * width * 0.5).toFloat())
                path.lineTo(width.toFloat(), (2 * cos(30 * 2 * Math.PI / 360) * width * 0.5).toFloat())
                path.close()
                canvas.drawPath(path, mPaint)
            }
        }
    }

    enum class Shape {
        TRIANGLE, CIRCLE, SQUARE
    }
}