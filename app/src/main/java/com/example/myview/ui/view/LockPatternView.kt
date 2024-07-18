package com.example.myview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.myview.ui.entity.Point
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 九宫格密码解锁自定义view
 */

interface LockPatternListener {
    fun lock(password: String)
}

class LockPatternView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    //[3][3] 二维数组
    private val mPoints: Array<Array<Point>> = Array(3) { Array(3) { Point(0, 0, 0) } }

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    // 外圆的半径
    private var mDotRadius: Int = 0

    // 画笔
    private lateinit var mLinePaint: Paint
    private lateinit var mPressedPaint: Paint
    private lateinit var mErrorPaint: Paint
    private lateinit var mNormalPaint: Paint
    private lateinit var mArrowPaint: Paint

    // 颜色
    private val mOuterPressedColor = 0xff8cbad8.toInt()
    private val mInnerPressedColor = 0xff0596f6.toInt()
    private val mOuterNormalColor = 0xffd9d9d9.toInt()
    private val mInnerNormalColor = 0xff929292.toInt()
    private val mOuterErrorColor = 0xff901032.toInt()
    private val mInnerErrorColor = 0xffea0945.toInt()

    private var mMovingX = 0f
    private var mMovingY = 0f

    private var mSelectBegin = false
    private var mIsErrorStatus = false

    private var mSelectPoints = mutableListOf<Point>()

    /**
     * 获取按下的点
     * @return 当前按下的点
     */
    private val point: Point?
        get() {
            for (i in mPoints.indices) {
                for (j in mPoints.indices) {
                    val point = mPoints[i][j]
                    if (checkInRound(point.centerX.toFloat(), point.centerY.toFloat(), mMovingX, mMovingY,mDotRadius.toFloat())) {
                        return point
                    }
                }
            }
            return null
        }

    private var mListener: LockPatternListener? = null

    fun setLockPatternListener(listener: LockPatternListener) {
        this.mListener = listener
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            initWidthAndHeight()
        }
    }

    private fun initWidthAndHeight() {
        mWidth = width
        mHeight = height

        var offsetX = 0
        var offsetY = 0

        if (mWidth > mHeight) {
            offsetX = (mWidth - mHeight) / 2
            mWidth = mHeight
        } else {
            offsetY = (mHeight - mWidth) / 2
            mHeight = mWidth
        }
        mDotRadius = mWidth / 12

        val padding = mDotRadius / 2
        val sideSize = (mWidth - 2 * padding) / 3
        offsetX += padding
        offsetY += padding

        for (i in mPoints.indices) {
            for (j in mPoints.indices) {
                // 循环初始化九个点
                mPoints[i][j] = Point(
                    offsetX + sideSize * (i * 2 + 1) / 2,
                    offsetY + sideSize * (j * 2 + 1) / 2,
                    i * mPoints.size + j
                )
            }
        }

        initPaint()
    }

    private fun initPaint() {
        // 线的画笔
        mLinePaint = Paint().apply {
            color = mInnerPressedColor
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = (mDotRadius / 9).toFloat()
        }
        // 按下的画笔
        mPressedPaint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = (mDotRadius / 6).toFloat()
        }
        // 错误的画笔
        mErrorPaint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = (mDotRadius / 6).toFloat()
        }
        // 默认的画笔
        mNormalPaint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = (mDotRadius / 9).toFloat()
        }
        // 箭头的画笔
        mArrowPaint = Paint().apply {
            color = mInnerPressedColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (i in mPoints.indices) {
            for (j in mPoints.indices) {
                val point = mPoints[i][j]
                // 循环绘制默认圆
                when (point.state) {
                    Point.State.NORMAL -> {
                        mNormalPaint.color = mOuterNormalColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius.toFloat(), mNormalPaint)
                        mNormalPaint.color = mInnerNormalColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius / 3.toFloat(), mNormalPaint)
                    }

                    Point.State.SELECT -> {
                        mPressedPaint.color = mOuterPressedColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius.toFloat(), mPressedPaint)
                        mPressedPaint.color = mInnerPressedColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius / 3.toFloat(), mPressedPaint)
                    }

                    else -> {
                        mErrorPaint.color = mOuterErrorColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius.toFloat(), mErrorPaint)
                        mErrorPaint.color = mInnerErrorColor
                        canvas.drawCircle(point.centerX.toFloat(), point.centerY.toFloat(), mDotRadius / 3.toFloat(), mErrorPaint)
                    }
                }
            }
        }

        drawLineToCanvas(canvas)
    }

    private fun drawLineToCanvas(canvas: Canvas) {
        if (mSelectPoints.size >= 1) {
            if (mIsErrorStatus) {
                mLinePaint.color = mInnerErrorColor
                mArrowPaint.color = mInnerErrorColor
            } else {
                mLinePaint.color = mInnerPressedColor
                mArrowPaint.color = mInnerPressedColor
            }

            var lastPoint = mSelectPoints[0]
            for (i in 1 until mSelectPoints.size) {
                val point = mSelectPoints[i]
                // 不断的画线
                drawLine(lastPoint, point, canvas, mLinePaint)
                drawArrow(canvas, mArrowPaint, lastPoint, point, (mDotRadius / 4).toFloat(), 38)
                lastPoint = point
            }

            val isInnerPoint = checkInRound(lastPoint.centerX.toFloat(), lastPoint.centerY.toFloat(), mMovingX, mMovingY,mDotRadius.toFloat())
            if (mSelectBegin && !isInnerPoint) {
                drawLine(lastPoint, Point(mMovingX.toInt(), mMovingY.toInt(), -1), canvas, mLinePaint)
            }
        }
    }

    /**
     * 画线
     */
    private fun drawLine(start: Point, end: Point, canvas: Canvas, paint: Paint) {
        val distance = distance(start.centerX.toDouble(), start.centerY.toDouble(), end.centerX.toDouble(), end.centerY.toDouble())
        val cosAngle = (end.centerX - start.centerX) / distance
        val sinAngle = (end.centerY - start.centerY) / distance
        val rx = (mDotRadius / 6 + mPressedPaint.strokeWidth) * cosAngle
        val ry = (mDotRadius / 6 + mPressedPaint.strokeWidth) * sinAngle
        canvas.drawLine(
            (start.centerX + rx).toFloat(), (start.centerY + ry).toFloat(),
            (end.centerX - rx).toFloat(), (end.centerY - ry).toFloat(),
            paint
        )
    }

    /**
     * 画箭头
     */
    private fun drawArrow(canvas: Canvas, paint: Paint, start: Point, end: Point, arrowHeight: Float, angle: Int) {
        val d = distance(start.centerX.toDouble(), start.centerY.toDouble(), end.centerX.toDouble(), end.centerY.toDouble())
        val sin_B = ((end.centerX - start.centerX) / d).toFloat()
        val cos_B = ((end.centerY - start.centerY) / d).toFloat()
        val tan_A = Math.tan(Math.toRadians(angle.toDouble())).toFloat()
        val h = (d - arrowHeight.toDouble() - mDotRadius * 1.1).toFloat()
        val l = arrowHeight * tan_A
        val a = l * sin_B
        val b = l * cos_B
        val x0 = h * sin_B
        val y0 = h * cos_B
        val x1 = start.centerX + (h + arrowHeight) * sin_B
        val y1 = start.centerY + (h + arrowHeight) * cos_B
        val x2 = start.centerX + x0 - b
        val y2 = start.centerY.toFloat() + y0 + a
        val x3 = start.centerX.toFloat() + x0 + b
        val y3 = start.centerY + y0 - a
        val path = Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mMovingX = event.x
        mMovingY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val firstPoint = point
                if (firstPoint != null) {
                    // 已经开始选点了
                    mSelectPoints.add(firstPoint)
                    // 点设置为已经选中
                    firstPoint.setStatusPressed()
                    // 开始绘制
                    mSelectBegin = true
                }
            }

            MotionEvent.ACTION_MOVE -> if (mSelectBegin) {
                val selectPoint = point
                if (selectPoint != null) {
                    selectPoint.setStatusPressed()
                    if (!mSelectPoints.contains(selectPoint)) {
                        // 把选中的点添加到集合
                        mSelectPoints.add(selectPoint)
                    }
                }
            }

            MotionEvent.ACTION_UP -> if (mSelectBegin) {
                if (mSelectPoints.size == 1) {
                    // 清空选择
                    clearSelectPoints()
                } else if (mSelectPoints.size <= 4) {
                    // 太短显示错误
                    showSelectError()
                } else {
                    // 成功回调
                    if (mListener != null) {
                        lockCallBack()
                    }
                }
                mSelectBegin = false
            }
        }

        invalidate()
        return true
    }

    /**
     * 回调
     */
    private fun lockCallBack() {
        var password = ""
        for (selectPoint in mSelectPoints) {
            password += selectPoint.index
        }
        mListener?.lock(password)
    }


    /**
     * 清空所有的点
     */
    private fun clearSelectPoints() {
        for (selectPoint in mSelectPoints) {
            selectPoint.setStatusNormal()
        }
        mSelectPoints.clear()
    }

    /**
     * 显示错误
     */
    fun showSelectError() {
        for (selectPoint in mSelectPoints) {
            selectPoint.setStatusError()
            mIsErrorStatus = true
        }

        postDelayed({
            clearSelectPoints()
            mIsErrorStatus = false
            invalidate()
        }, 1000)
    }

    /**
     * 检查是否在圆内（包括圆上）
     */
    private fun checkInRound(centerX: Float, centerY: Float,  x: Float, y: Float,radius: Float): Boolean {
        val dx = x - centerX
        val dy = y - centerY
        return sqrt(dx.pow(2) + dy.pow(2)) <= radius
    }

    /**
     * 计算圆心之间的距离
     */
    private fun distance(startX: Double, startY: Double, endX: Double, endY: Double): Double {
        return sqrt((endX - startX).pow(2.0) + (endY - startY).pow(2.0))
    }
}