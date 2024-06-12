package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView
import com.example.myview.R

class ColorTrackTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : TextView(context, attrs, defStyleAttr) {

    private var mOriginColor: Int
    private var mChangeColor: Int
    private var mOriginPaint: Paint
    private var mChangePaint: Paint
    var mDirection: Direction = Direction.LEFT_TO_RIGHT
    var mProgress = 0.0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView)
        mOriginColor = ta.getColor(R.styleable.ColorTrackTextView_color_text_view_origin_color, Color.BLACK)
        mChangeColor = ta.getColor(R.styleable.ColorTrackTextView_color_text_view_change_color, Color.RED)
        mOriginPaint = getPaintByColor(mOriginColor)
        mChangePaint = getPaintByColor(mChangeColor)
        ta.recycle()
    }

    private fun getPaintByColor(color: Int): Paint {
        return Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            textSize = this@ColorTrackTextView.textSize
            setColor(color)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val middle = (mProgress * width).toInt()
        if (mDirection == Direction.LEFT_TO_RIGHT) {
            drawText(canvas, mChangePaint, 0, middle)
            drawText(canvas, mOriginPaint, middle, width)
        } else {
            drawText(canvas, mOriginPaint, width - middle, width)
            drawText(canvas, mChangePaint, 0, width - middle)
        }
    }

    private fun drawText(canvas: Canvas?, paint: Paint, start: Int, end: Int) {
        canvas?.save()
        val clipRect = Rect(start, 0, end, height)
        canvas?.clipRect(clipRect)
        val text = text.toString()
        val fontMetricsInt = paint.fontMetricsInt
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val startX = width * 0.5f - bounds.width() * 0.5f
        val dy = (fontMetricsInt.bottom - fontMetricsInt.top) * 0.5f - fontMetricsInt.bottom
        val baseLineY = height * 0.5f + dy
        canvas?.drawText(text, startX, baseLineY, paint)
        canvas?.restore()
    }

    enum class Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

}