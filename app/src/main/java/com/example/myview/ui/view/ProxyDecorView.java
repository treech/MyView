package com.example.myview.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myview.R;

public class ProxyDecorView extends ConstraintLayout {

    private static final String TAG = "ProxyDecorView";

    private Window mWindow;
    private int mStatusBarHeight = 0;
    private int mDragHotArea = 0;
    private boolean mDragAnimating = false;

    /**
     * 从自定义属性获取
     */
    private boolean mIgnoreClick = false;
    /**
     * 从自定义属性获取
     */
    private boolean mShowIndicator = true;

    private int mDragBeginX = 0;
    private int mDragBeginY = 0;
    private long mDragBeginTime = 0L;
    private boolean mDragStarting = false;
    private boolean mDragDown = false;
    private static final int STATE_HIDE = 0;
    private static final int STATE_NORMAL = 102;
    private static final int STATE_FOCUSED = 255;

    private static final int CLOSE_WAY_DEFAULT = 0;
    private static final int CLOSE_WAY_MOVETOBACK = 1;
    private static final int CLOSE_WAY_FINISH = 2;
    private int mIndicatorCloseWay = CLOSE_WAY_MOVETOBACK;

    private int mIndicatorState = -1;
    private ImageView mIndicatorImageView;

    private Handler mHandler = new Handler();

    public ProxyDecorView(@NonNull Context context) {
        this(context, null);
    }

    public ProxyDecorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProxyDecorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIndicatorImageView = findViewById(R.id.iv_home_bar_indicator);
        if (mIndicatorImageView != null) {
            mIndicatorImageView.setImageAlpha(STATE_NORMAL);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDragAnimating) {
            return true;
        }
        Log.i(TAG, "dispatchTouchEvent y:" + ev.getY() + " dragHotArea:" + mDragHotArea + " dragDown:" + mDragDown);
        if (!mDragDown && (int) ev.getY() > mDragHotArea) {
            return super.dispatchTouchEvent(ev);
        }
        if (mShowIndicator) {
            if (handlerForDragDismiss(ev)) {
                if (!mIgnoreClick) {
                    return true;
                }
            }
        }
        return (int) ev.getY() <= mDragHotArea || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent event:" + event);
        return super.onTouchEvent(event);
    }

    public void setWindow(Window window) {
        mWindow = window;
        if (mShowIndicator) {
            initHotArea();
        }
    }

    private void initHotArea() {
        if (mStatusBarHeight == 0) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mStatusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
//        mDragHotArea = mStatusBarHeight + dp2Px(18);
        mDragHotArea = dp2Px(18);

        Log.i(TAG, "statusBarHeight:" + mStatusBarHeight + " dragHotArea:" + mDragHotArea);
    }

    private int dp2Px(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    private void updateIndicatorState(int state) {
        Log.i(TAG, "updateIndicatorState state:" + state + " indicatorImageView:" + mIndicatorImageView + " indicatorState:" + mIndicatorState);
        if (mIndicatorImageView != null) {
            if (mIndicatorState != state) {
                mIndicatorState = state;
                mIndicatorImageView.setImageAlpha(state);
            }
        }
    }

    private boolean handlerForDragDismiss(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            if ((int) event.getY() <= mDragHotArea) {
                mDragBeginX = (int) event.getRawX();
                mDragBeginY = (int) event.getRawY();
                mDragBeginTime = System.currentTimeMillis();
                mDragDown = true;
                mDragStarting = false;
                updateIndicatorState(STATE_FOCUSED);
                return false;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mDragDown) {
                int newTop = (int) event.getRawY() - mDragBeginY;
                if (mDragStarting) {
                    dragUpdateTop(newTop);
                    return true;
                } else {
                    if (newTop > 15) {
                        mDragStarting = true;
                        dragUpdateTop(newTop);
                        return true;
                    }
                }
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            Log.i(TAG, "dragDown:" + mDragDown + " dragStarting:" + mDragStarting);
            if (mDragDown || mDragStarting) {
                int upX = (int) event.getRawX();
                int upY = (int) event.getRawY();
                boolean ignoreClick = (int) event.getY() > mDragHotArea;
                boolean willDismiss = calculateDragDismiss(upX, upY, ignoreClick);
                startDismissAnimation(willDismiss);
                mDragBeginX = 0;
                mDragBeginY = 0;
                mDragBeginTime = 0;
                mDragStarting = false;
                mDragDown = false;
                updateIndicatorState(STATE_NORMAL);
                return willDismiss;
            }
        }
        return false;
    }

    private void dragUpdateTop(int top) {
        if (top < 0) {
            top = 0;
        }
        setTranslationY(top);
    }

    private void startDismissAnimation(boolean willDismiss) {
        int begin = (int) getTranslationY();
        int end = willDismiss ? getHeight() : 0;
        int duration = willDismiss ? ((end - begin) / 3) : ((begin - end) / 3);
        if (duration < 0) {
            Log.e(TAG, "Error of negative duration:" + duration + " willDismiss:" + willDismiss + " begin:" + begin + " end:" + end);
        }
        duration = 5;

        ValueAnimator anim = ValueAnimator.ofInt(begin, end);
        anim.setDuration(duration);
        anim.addUpdateListener(valueAnim -> {
            int value = (int) valueAnim.getAnimatedValue();
            setTranslationY(value);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (willDismiss) {
                    Activity activity = (Activity) mWindow.getContext();
                    if (mIndicatorCloseWay == CLOSE_WAY_MOVETOBACK) {
                        moveToBack(activity);
                    } else {
                        activity.finish();
                    }
                }
                mDragAnimating = false;
            }
        });
        mDragAnimating = true;
        anim.start();
    }

    private void moveToBack(final Activity activity) {
        boolean ret = activity.moveTaskToBack(!activity.isTaskRoot());
        Log.i(TAG, "moveToBack activity:" + activity + " ret:" + ret);
        postDelayed(() -> {
            setTranslationY(0);
        }, 500);
    }

    private boolean calculateDragDismiss(int x, int y, boolean ignoreClick) {
        boolean willDismiss = false;
        //1: check click
        int dx = x - mDragBeginX;
        int dy = y - mDragBeginY;
        if (!mIgnoreClick && !ignoreClick) {
            if (dx > -10 && dx < 10 && dy > -10 && dy < 10) {
                willDismiss = false;
            }
        }

        //2: check speed
        if (!willDismiss) {
            long now = System.currentTimeMillis();
            float v = ((float) (dy * 1000)) / (now - mDragBeginTime);
            if (v > 500.0f) {
                willDismiss = true;
            }
        }

        //3: check distance
        if (!willDismiss) {
            if (dy > 300) {
                willDismiss = true;
            }
        }
        return willDismiss;
    }
}
