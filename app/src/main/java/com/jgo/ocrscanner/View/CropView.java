package com.jgo.ocrscanner.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.jgo.ocrscanner.R;
import com.jgo.ocrscanner.utils.JgoLogUtil;
import com.jgo.ocrscanner.utils.ScreenUtils;

/**
 * Created by ke-oh on 2019/08/18.
 *
 */

public class CropView extends FrameLayout {

    private static final String TAG = CropView.class.getSimpleName();

    private static final float PARENT_MARGIN = 13;
    private static final float RADIUS = 10;
    private float mParentMargin = ScreenUtils.dip2px(PARENT_MARGIN);
    private float mPointRadius = ScreenUtils.dip2px(RADIUS);
    private Context mContext;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    private CropPoint leftTopPoint;
    private CropPoint rightTopPoint;
    private CropPoint leftBottomPoint;
    private CropPoint rightBottomPoint;

    public CropView(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CropView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(PARENT_MARGIN - RADIUS);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //topLine
        canvas.drawLine(mParentMargin, mParentMargin, mWidth - mParentMargin, mParentMargin, mPaint);
        //rightLine
        canvas.drawLine(mWidth - mParentMargin, mParentMargin, mWidth - mParentMargin, mHeight - mParentMargin, mPaint);
        //bottomLine
        canvas.drawLine(mWidth - mParentMargin, mHeight - mParentMargin, mParentMargin, mHeight - mParentMargin, mPaint);
        //leftLine
        canvas.drawLine(mParentMargin, mHeight - mParentMargin, mParentMargin, mParentMargin, mPaint);

        canvas.drawCircle(mParentMargin, mParentMargin, mPointRadius, mPaint);

        canvas.drawCircle(mWidth - mParentMargin, mParentMargin, mPointRadius, mPaint);

        canvas.drawCircle(mWidth - mParentMargin, mHeight - mParentMargin, mPointRadius, mPaint);

        canvas.drawCircle(mParentMargin, mHeight - mParentMargin, mPointRadius, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        JgoLogUtil.d(TAG, "onTouchEvent rawX : " + x + ", rawY : " + y);
        return super.onTouchEvent(event);
    }

    private class CropPoint {
        public float x;
        public float y;

        CropPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
