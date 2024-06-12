package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.example.myview.R

interface OnTouchLetterCallback {
    fun fingerDown()
    fun callback(position: Int, letter: String)
    fun fingerUp()
}

class SliderBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var mDefaultLetterPaint: Paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
    private var mFocusLetterPaint: Paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
    private var listener: OnTouchLetterCallback? = null

    private var mTextSize: Int
    private var mTextColorDefault: Int
    private var mTextColorFocus: Int
    private var mLayoutWidth: Int
    private val mTempRect = Rect()

    private var mCurrentLetter: String = LETTERS[0]
    private var mItemHeight: Int = 0

    fun setOnTouchLetterListener(listener: OnTouchLetterCallback) {
        this.listener = listener
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SliderBarView)
        mTextSize = ta.getDimensionPixelSize(R.styleable.SliderBarView_text_size, sp2Px(14f))
        mTextColorDefault = ta.getColor(R.styleable.SliderBarView_text_color_default, Color.BLACK)
        mTextColorFocus = ta.getColor(R.styleable.SliderBarView_text_color_focus, Color.RED)
        ta.recycle()

        mDefaultLetterPaint.apply {
            textSize = mTextSize.toFloat()
            color = mTextColorDefault
        }

        mFocusLetterPaint.apply {
            textSize = mTextSize.toFloat()
            color = mTextColorFocus
        }

        mDefaultLetterPaint.getTextBounds("W", 0, 1, mTempRect)
        mLayoutWidth = paddingLeft + mTempRect.width() + paddingRight
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.action==MotionEvent.ACTION_DOWN){
                    listener?.fingerDown()
                }
                val position = when {
                    event.y < 0 -> 0
                    event.y > height -> LETTERS.size - 1
                    else -> (event.y / mItemHeight).toInt()
                }
                val letter = LETTERS[position]
                listener?.callback(position, letter)
                if (letter == mCurrentLetter) {
                    return true
                }
                invalidate()
                mCurrentLetter = letter
                Log.i(TAG, "onTouchEvent y:${event.y}")
            }

            MotionEvent.ACTION_UP -> {
                listener?.fingerUp()
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(mLayoutWidth, measureHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mItemHeight = (height - paddingTop - paddingBottom) / LETTERS.size
    }

    override fun onDraw(canvas: Canvas) {
        val itemHeight = height / LETTERS.size
        LETTERS.forEachIndexed { index, s ->
            val letterWidth = mDefaultLetterPaint.measureText(s)
            val startX = width * 0.5f - letterWidth * 0.5f - paddingLeft
            val fontMetricsInt = mDefaultLetterPaint.fontMetricsInt
            val dy = (fontMetricsInt.bottom - fontMetricsInt.top) * 0.5f - fontMetricsInt.bottom
            val baseLineY = itemHeight * 0.5f + index * itemHeight + dy
            canvas.drawText(s, startX, baseLineY, if (s == mCurrentLetter) mFocusLetterPaint else mDefaultLetterPaint)
        }
    }

    private fun sp2Px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()
    }

    companion object {
        private const val TAG = "SliderBarView"
        val LETTERS = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    }
}