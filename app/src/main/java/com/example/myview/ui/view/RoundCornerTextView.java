package com.example.myview.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class RoundCornerTextView extends AppCompatTextView {

    private final RectF roundRect = new RectF();
    private final Paint desPaint = new Paint();
    private final Paint srcPaint = new Paint();
    private float mRadius = 10;
    private boolean isChange = false;

    public RoundCornerTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public RoundCornerTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundCornerTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void change(boolean isChange) {
        this.isChange = isChange;
        invalidate();
    }

    public void init() {
        desPaint.setAntiAlias(true);//设置抗锯齿
        desPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        srcPaint.setAntiAlias(true);
        float density = getResources().getDisplayMetrics().density;
        mRadius *= density;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        roundRect.set(0, 0, width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        //保存最原始的roundRect
        canvas.saveLayer(roundRect, srcPaint, Canvas.ALL_SAVE_FLAG);
        if (isChange) {
            //保存去掉头部2圆角的roundRect(实际就是保留底部的2个圆角)
            canvas.drawRect(roundRect.left, (roundRect.top + roundRect.bottom) / 2, roundRect.right, roundRect.bottom, srcPaint);
            //保存去掉底部2圆角的roundRect(实际就是保留顶部的2个圆角)
//            canvas.drawRect(roundRect.left, roundRect.top, roundRect.right, roundRect.bottom / 2, srcPaint);
        }
        //保存掉头部2圆角的roundRect
        canvas.drawRoundRect(roundRect, mRadius, mRadius, srcPaint);
        //保存叠加后的内容
        canvas.saveLayer(roundRect, desPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        //清空所有的图像矩阵修改状态
        canvas.restore();
    }
}
