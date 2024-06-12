package com.example.myview.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myview.databinding.ActivityMainBinding
import com.example.myview.ui.view.ColorTrackTextView
import com.example.myview.ui.view.LoadingView
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mShape = LoadingView.Shape.SQUARE

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (mShape) {
                LoadingView.Shape.TRIANGLE -> {
                    binding.loadingView.mShape = LoadingView.Shape.CIRCLE
                    mShape = LoadingView.Shape.CIRCLE
                    sendEmptyMessageDelayed(0, 1000L)
                }

                LoadingView.Shape.CIRCLE -> {
                    binding.loadingView.mShape = LoadingView.Shape.SQUARE
                    mShape = LoadingView.Shape.SQUARE
                    sendEmptyMessageDelayed(0, 1000L)
                }

                else -> {
                    binding.loadingView.mShape = LoadingView.Shape.TRIANGLE
                    mShape = LoadingView.Shape.TRIANGLE
                    sendEmptyMessageDelayed(0, 1000L)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btBack.setOnClickListener { finish() }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun hookTest(view: View) {
        val toast = Toast.makeText(this, "abc", Toast.LENGTH_LONG)
        try {
            val toastClass = Toast::class.java
            val getServiceMethod = toastClass.getDeclaredMethod("getService")
            getServiceMethod.isAccessible = true
            val service = getServiceMethod.invoke(toast)

            val INotificationManager = Class.forName("android.app.INotificationManager")

            val proxy: Any = Proxy.newProxyInstance(
                Thread::class.java.classLoader, arrayOf<Class<*>>(INotificationManager::class.java),
                InvocationHandler { proxy, method, args -> // 判断enqueueToast()方法时执行操作
                    if (method.name == "enqueueToast") {
                        Log.e("hook", method.name)
                        getContent(args[1])
                    }
                    method.invoke(service, args)
                })
            val sServiceField = toastClass.getDeclaredField("sService")
            sServiceField.isAccessible = true
            // 用代理对象给sService赋值
            sServiceField.set(null, proxy)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        toast.show()
    }


    @Throws(ClassNotFoundException::class, NoSuchFieldException::class, IllegalAccessException::class)
    private fun getContent(arg: Any) {
        // 获取TN的class
        val tnClass = Class.forName(Toast::class.java.name + "\$TN")
        // 获取mNextView的Field
        val mNextViewField: Field = tnClass.getDeclaredField("mNextView")
        mNextViewField.isAccessible = true
        // 获取mNextView实例
        val mNextView = mNextViewField.get(arg) as LinearLayout
        // 获取textview
        val childView = mNextView.getChildAt(0) as TextView
        // 获取文本内容
        val text = childView.text
        // 替换文本并赋值
        childView.text = text.toString().replace("HookToast：", "")
        Log.e("hook", "content: " + childView.text)
    }

    fun leftToRight(view: View) {
        binding.colorTextView.mDirection = ColorTrackTextView.Direction.LEFT_TO_RIGHT
        ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            addUpdateListener {
                binding.colorTextView.mProgress = it.animatedValue as Float
            }
            start()
        }
    }

    fun rightToLeft(view: View) {
        binding.colorTextView.mDirection = ColorTrackTextView.Direction.RIGHT_TO_LEFT
        ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            addUpdateListener {
                binding.colorTextView.mProgress = it.animatedValue as Float
            }
            start()
        }
    }

    fun startSwipe(view: View) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = AccelerateInterpolator()
            duration = 1500
            addUpdateListener {
                binding.colorProgressView.progress = it.animatedValue as Float
            }
            start()
        }
    }

    fun loadingView(view: View) {
        handler.removeMessages(0)
        handler.sendEmptyMessage(0)
    }
}