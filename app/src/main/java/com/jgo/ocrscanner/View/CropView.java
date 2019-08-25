package com.jgo.ocrscanner.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
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

    private static final float PARENT_MARGIN = 10;
    private static final float RADIUS = 10;
    private static final float CORNER_LENGTH = ScreenUtils.dip2px(20);
    private static final float CORNER_PAINT_WIDTH = ScreenUtils.dip2px(4);

    private float mParentMarginLeft = ScreenUtils.dip2px(PARENT_MARGIN);
    private float mParentMarginTop = ScreenUtils.dip2px(PARENT_MARGIN);
    private float mParentMarginRight = ScreenUtils.dip2px(PARENT_MARGIN);
    private float mParentMarginBottom = ScreenUtils.dip2px(PARENT_MARGIN);
    private float mPointRadius = ScreenUtils.dip2px(RADIUS);
    private Context mContext;
    private Paint mPaint;
    private Path mCropPath;
    private int mWidth;
    private int mHeight;

    private CropPoint mLeftTopPoint = new CropPoint();
    private CropPoint mRightTopPoint = new CropPoint();
    private CropPoint mLeftBottomPoint = new CropPoint();
    private CropPoint mRightBottomPoint = new CropPoint();

    float mDownX = 0.0f;
    float mDownY = 0.0f;

    private CropPoint mBackupPointForMove = new CropPoint();

    private enum TOUCH_CORNER {
        LEFT_TOP,
        RIGHT_TOP,
        RIGHT_BOTTOM,
        LEFT_BOTTOM,
        NONE
    }
    private TOUCH_CORNER mTouchCorner;

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
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCropPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mLeftTopPoint.setX(mParentMarginLeft);
        mLeftTopPoint.setY(mParentMarginTop);

        mRightTopPoint.setX(mWidth - mParentMarginRight);
        mRightTopPoint.setY(mParentMarginTop);

        mRightBottomPoint.setX(mWidth - mParentMarginRight);
        mRightBottomPoint.setY(mHeight - mParentMarginBottom);

        mLeftBottomPoint.setX(mParentMarginLeft);
        mLeftBottomPoint.setY(mHeight - mParentMarginBottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        mPaint.setStrokeWidth(PARENT_MARGIN - RADIUS);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.FILL);

        //canvas.drawBitmap(mBackgroudBitmap, 0, 0, mPaint);
        //topLine
        /*canvas.drawLine(mLeftTopPoint.x, mLeftTopPoint.y, mRightTopPoint.x, mRightTopPoint.y, mPaint);
        //rightLine
        canvas.drawLine(mRightTopPoint.x, mRightTopPoint.y, mRightBottomPoint.x, mRightBottomPoint.y, mPaint);
        //bottomLine
        canvas.drawLine(mRightBottomPoint.x, mRightBottomPoint.y, mLeftBottomPoint.x, mLeftBottomPoint.y, mPaint);
        //leftLine
        canvas.drawLine(mLeftBottomPoint.x, mLeftBottomPoint.y, mLeftTopPoint.x, mLeftTopPoint.y, mPaint);*/

        mCropPath.reset();
        mCropPath.moveTo(mLeftTopPoint.x, mLeftTopPoint.y);
        mCropPath.lineTo(mRightTopPoint.x, mRightTopPoint.y);
        mCropPath.lineTo(mRightBottomPoint.x, mRightBottomPoint.y);
        mCropPath.lineTo(mLeftBottomPoint.x, mLeftBottomPoint.y);
        canvas.drawPath(mCropPath, mPaint);

        mPaint.setColor(ContextCompat.getColor(mContext, R.color.transparent));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(mLeftTopPoint.x, mRightTopPoint.y, mRightBottomPoint.x, mLeftBottomPoint.y, mPaint);

        mPaint.setStrokeWidth(CORNER_PAINT_WIDTH);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        //topLeftCorner-H
        canvas.drawLine(mLeftTopPoint.x - CORNER_PAINT_WIDTH, mLeftTopPoint.y - CORNER_PAINT_WIDTH / 2,
                mLeftTopPoint.x - CORNER_PAINT_WIDTH + CORNER_LENGTH, mLeftTopPoint.y - CORNER_PAINT_WIDTH / 2, mPaint);
        //topLeftCorner-V
        canvas.drawLine(mLeftTopPoint.x - CORNER_PAINT_WIDTH / 2, mLeftTopPoint.y - CORNER_PAINT_WIDTH,
                mLeftTopPoint.x - CORNER_PAINT_WIDTH / 2, mLeftTopPoint.y - CORNER_PAINT_WIDTH + CORNER_LENGTH, mPaint);

        //rightTopCorner-H
        canvas.drawLine(mRightTopPoint.x + CORNER_PAINT_WIDTH, mRightTopPoint.y - CORNER_PAINT_WIDTH / 2,
                mRightTopPoint.x + CORNER_PAINT_WIDTH - CORNER_LENGTH, mRightTopPoint.y - CORNER_PAINT_WIDTH / 2, mPaint);
        //rightTopCorner-V
        canvas.drawLine(mRightTopPoint.x + CORNER_PAINT_WIDTH / 2, mRightTopPoint.y - CORNER_PAINT_WIDTH,
                mRightTopPoint.x + CORNER_PAINT_WIDTH / 2, mRightTopPoint.y - CORNER_PAINT_WIDTH + CORNER_LENGTH, mPaint);

        //rightBottomCorner-H
        canvas.drawLine(mRightBottomPoint.x + CORNER_PAINT_WIDTH, mRightBottomPoint.y + CORNER_PAINT_WIDTH / 2,
                mRightBottomPoint.x + CORNER_PAINT_WIDTH - CORNER_LENGTH, mRightBottomPoint.y + CORNER_PAINT_WIDTH / 2, mPaint);
        //rightBottomCorner-V
        canvas.drawLine(mRightBottomPoint.x + CORNER_PAINT_WIDTH / 2, mRightBottomPoint.y + CORNER_PAINT_WIDTH,
                mRightBottomPoint.x + CORNER_PAINT_WIDTH / 2, mRightBottomPoint.y + CORNER_PAINT_WIDTH - CORNER_LENGTH, mPaint);

        //leftBottomCorner-H
        canvas.drawLine(mLeftBottomPoint.x - CORNER_PAINT_WIDTH, mLeftBottomPoint.y + CORNER_PAINT_WIDTH / 2,
                mLeftBottomPoint.x - CORNER_PAINT_WIDTH + CORNER_LENGTH, mLeftBottomPoint.y + CORNER_PAINT_WIDTH / 2, mPaint);
        //leftBottomCorner-V
        canvas.drawLine(mLeftBottomPoint.x - CORNER_PAINT_WIDTH / 2, mLeftBottomPoint.y + CORNER_PAINT_WIDTH,
                mLeftBottomPoint.x - CORNER_PAINT_WIDTH / 2, mLeftBottomPoint.y + CORNER_PAINT_WIDTH - CORNER_LENGTH, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        boolean isTapCorner = false;
        float movedX = 0.0f;
        float movedY = 0.0f;
        JgoLogUtil.d(TAG, "onTouchEvent getX : " + touchX + ", getY : " + touchY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                JgoLogUtil.d(TAG, "onTouchEvent ACTION_DOWN");
                mTouchCorner = getTapCorner(touchX, touchY);

                if (mTouchCorner == TOUCH_CORNER.NONE) {
                    isTapCorner = false;
                } else {
                    isTapCorner = true;
                    mDownX = touchX;
                    mDownY = touchY;

                    switch (mTouchCorner) {
                        case LEFT_TOP:
                            mBackupPointForMove.setX(mLeftTopPoint.x);
                            mBackupPointForMove.setY(mLeftTopPoint.y);
                            break;
                        case RIGHT_TOP:
                            mBackupPointForMove.setX(mRightTopPoint.x);
                            mBackupPointForMove.setY(mRightTopPoint.y);
                            break;
                        case RIGHT_BOTTOM:
                            mBackupPointForMove.setX(mRightBottomPoint.x);
                            mBackupPointForMove.setY(mRightBottomPoint.y);
                            break;
                        case LEFT_BOTTOM:
                            mBackupPointForMove.setX(mLeftBottomPoint.x);
                            mBackupPointForMove.setY(mLeftBottomPoint.y);
                            break;
                    }

                }
                break;
            case MotionEvent.ACTION_UP :
                JgoLogUtil.d(TAG, "onTouchEvent ACTION_UP");
                mDownX = 0.0f;
                mDownY = 0.0f;
                break;
            case MotionEvent.ACTION_CANCEL :
                JgoLogUtil.d(TAG, "onTouchEvent ACTION_CANCEL");
                mDownX = 0.0f;
                mDownY = 0.0f;
                break;
            case MotionEvent.ACTION_MOVE :
                JgoLogUtil.d(TAG, "onTouchEvent ACTION_MOVE");
                movedX = touchX - mDownX;
                movedY = touchY - mDownY;

                float diffX = mBackupPointForMove.x + movedX;
                float diffY = mBackupPointForMove.y + movedY;
                switch (mTouchCorner) {
                    case LEFT_TOP:
                        mLeftTopPoint.setX(diffX);
                        mLeftTopPoint.setY(diffY);

                        mRightTopPoint.setY(diffY);
                        mLeftBottomPoint.setX(diffX);
                        break;
                    case RIGHT_TOP:
                        mRightTopPoint.setX(diffX);
                        mRightTopPoint.setY(diffY);

                        mLeftTopPoint.setY(diffY);
                        mRightBottomPoint.setX(diffX);
                        break;
                    case RIGHT_BOTTOM:
                        mRightBottomPoint.setX(diffX);
                        mRightBottomPoint.setY(diffY);

                        mRightTopPoint.setX(diffX);
                        mLeftBottomPoint.setY(diffY);
                        break;
                    case LEFT_BOTTOM:
                        mLeftBottomPoint.setX(diffX);
                        mLeftBottomPoint.setY(diffY);

                        mLeftTopPoint.setX(diffX);
                        mRightBottomPoint.setY(diffY);
                        break;
                }
                JgoLogUtil.d(TAG, "onTouchEvent ACTION_MOVE movedX : " + movedX + ", movedY : " + movedY);
                invalidate();
                break;
        }
        return isTapCorner;
    }

    private TOUCH_CORNER getTapCorner(float x, float y) {

        if (mPointRadius >= Math.abs(mLeftTopPoint.x - x) && mPointRadius > Math.abs(mLeftTopPoint.y - y)) {
            return TOUCH_CORNER.LEFT_TOP;
        } else if (mPointRadius >= Math.abs(mRightTopPoint.x - x) && mPointRadius > Math.abs(mRightTopPoint.y - y)) {
            return TOUCH_CORNER.RIGHT_TOP;
        } else if (mPointRadius >= Math.abs(mRightBottomPoint.x - x) && mPointRadius > Math.abs(mRightBottomPoint.y - y)) {
            return TOUCH_CORNER.RIGHT_BOTTOM;
        } else if (mPointRadius >= Math.abs(mLeftBottomPoint.x - x) && mPointRadius > Math.abs(mLeftBottomPoint.y - y)) {
            return TOUCH_CORNER.LEFT_BOTTOM;
        } else {
            return TOUCH_CORNER.NONE;
        }
    }

    private class CropPoint {
        public float x;
        public float y;

        CropPoint() {

        }

        CropPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setXDiff(float diffX) {
            this.x += diffX;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setYDiff(float diffY) {
            this.y += diffY;
        }

    }
}
