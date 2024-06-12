package com.example.myview.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import com.example.myview.R
import com.example.myview.ui.Util

/**
 * 仿QQ6.0主页面侧滑的自定View
 */
class SlidingMenu @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    HorizontalScrollView(context, attrs, defStyleAttr) {

    /**
     * menu布局padding
     */
    private var mMenuRightPadding = 0f

    private var mMenuWidth = 0f

    private var mIsMenuOpen = false

    private var mGestureDetector: GestureDetector

    private lateinit var mMenuView: View
    private lateinit var mShadowView: ImageView

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu)
        // 获取自定义的右边留出的宽度
        mMenuRightPadding = ta.getDimension(R.styleable.SlidingMenu_menu_right_padding, Util.dp2Px(context, 50f))
        // 计算菜单的宽度 = 屏幕的宽度 - 自定义右边留出的宽度
        mMenuWidth = getScreenWidth() - mMenuRightPadding
        ta.recycle()
        // 实例化手势处理类
        mGestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // 1.获取根View也就是外层的LinearLayout
        val container = getChildAt(0) as ViewGroup
        if (container.childCount > 2) {
            // 里面只允许放置两个布局  一个是Menu(菜单布局) 一个是Content（主页内容布局）
            throw IllegalStateException("SlidingMenu can host only two direct children")
        }

        // 2.获取菜单和内容布局
        mMenuView = container.getChildAt(0)
        // 3.指定内容和菜单布局的宽度
        // 3.1 菜单的宽度 = 屏幕的宽度 - 自定义的右边留出的宽度
        mMenuView.layoutParams.apply {
            width = mMenuWidth.toInt()
        }
        val contentView = container.getChildAt(1)
        container.removeView(contentView)

        val newContentView = FrameLayout(context)
        newContentView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        newContentView.addView(contentView)

        mShadowView = ImageView(context)
        mShadowView.setBackgroundColor(Color.parseColor("#99000000"))
        newContentView.addView(mShadowView)

        container.addView(newContentView)

        // 3.2 内容的宽度 = 屏幕的宽度
        newContentView.layoutParams.apply {
            width = getScreenWidth()
        }
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val container = getChildAt(0) as ViewGroup
//        if (container.childCount > 2) {
//            throw IllegalStateException("SlidingMenu can host only two direct children")
//        }
//
//        val menuView = container.getChildAt(0)
//        menuView.layoutParams.apply {
//            width = mMenuWidth.toInt()
//        }
//        val contentView = container.getChildAt(1)
//        contentView.layoutParams.apply {
//            width = getScreenWidth()
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        // l 是 当前滚动的x距离  在滚动的时候会不断的回调这个方法
        // 6. 实现菜单左边抽屉样式的动画效果
        Log.i("SlidingMenu","l:$l")
        mMenuView.translationX = l * 0.8f

        // 7.给内容添加阴影效果 - 计算梯度值
        val gradientValue = l * 1f / mMenuWidth // 这是  1 - 0 变化的值

        // 7.给内容添加阴影效果 - 给阴影的View指定透明度   0 - 1 变化的值
        val shadowAlpha = 1 - gradientValue
        mShadowView.setAlpha(shadowAlpha)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // 布局指定后会从新摆放子布局，当其摆放完毕后，让菜单滚动到不可见状态
        if (changed) {
            scrollTo(mMenuWidth.toInt(), 0)
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 处理手指快速滑动
        if (mGestureDetector.onTouchEvent(ev)) {
            return true
        }
        when (ev.action) {
            MotionEvent.ACTION_UP -> {
                // 手指抬起获取滚动的位置
                Log.i("SlidingMenu", "scrollX:$scrollX,MenuWidth:$mMenuWidth")
                if (scrollX > mMenuWidth / 2) {
                    // 关闭菜单
                    closeMenu()
                } else {
                    // 打开菜单
                    openMenu()
                }
                return false
            }
        }
        return super.onTouchEvent(ev)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.i("SlidingMenu", "velocityX:$velocityX")
            if (mIsMenuOpen) {
                //如果已经打开，向左滑动
                if (velocityX < -500) {
                    toggleMenu()
                    return true
                }
            } else {
                //如果已经关闭，向右滑动
                if (velocityX > 500) {
                    toggleMenu()
                    return true
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    private fun toggleMenu() {
        if (mIsMenuOpen) {
            closeMenu()
        } else {
            openMenu()
        }
    }

    private fun openMenu() {
        smoothScrollTo(0, 0)
        mIsMenuOpen = true
    }

    private fun closeMenu() {
        smoothScrollTo(mMenuWidth.toInt(), 0)
        mIsMenuOpen = false
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }

    private fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }
}