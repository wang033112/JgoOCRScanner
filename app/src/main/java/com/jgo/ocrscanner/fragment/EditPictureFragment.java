package com.jgo.ocrscanner.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jgo.ocrscanner.R;
import com.jgo.ocrscanner.View.CropView;
import com.jgo.ocrscanner.tess.TessManager;
import com.jgo.ocrscanner.utils.ScreenUtils;

/**
 * Created by ke-oh on 2019/08/18.
 *
 */

public class EditPictureFragment extends Fragment implements View.OnClickListener, TakePictureFragment.OnTakePictureListener {

    private static final String TAG = EditPictureFragment.class.getSimpleName();

    private Bitmap mEditBitmap;
    private ImageView mEditImageView;
    private CropView mCropView;

    private ViewGroup mDetectResultLayout;
    private TextView mDetectResultTV;
    private ProgressBar mDetectingPb;

    private HandlerThread mDetectThread;
    private Handler mDetectHandler;
    private Handler mUIHandler;

    private boolean mIsShowResultBmp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_picture, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditImageView = view.findViewById(R.id.edit_picture_im);
        if (mEditBitmap != null) {

            ViewGroup.LayoutParams layoutParams = mEditImageView.getLayoutParams();
            layoutParams.width = mEditBitmap.getWidth() * 5 / 6 ;
            layoutParams.height = mEditBitmap.getHeight() * 5 / 6;

            mEditImageView.setLayoutParams(layoutParams);
            mEditImageView.setImageBitmap(mEditBitmap);

        }

        mCropView = view.findViewById(R.id.crop_picture_view);
        mDetectResultLayout = view.findViewById(R.id.detect_result_layout);
        mDetectResultTV = view.findViewById(R.id.detect_result_tv);
        mDetectingPb = view.findViewById(R.id.picture_detecting_pb);

        mDetectThread = new HandlerThread("DetectThread");
        mDetectThread.start();

        mDetectHandler = new Handler(mDetectThread.getLooper());
        mUIHandler = new Handler();

    }

    public void cropPicture() {

        if (mIsShowResultBmp) {
            return;
        }

        //getBitmap
        final Bitmap cropBitmap = getBitmap();
        mEditImageView.setImageBitmap(cropBitmap);
        mCropView.setVisibility(View.GONE);
        mIsShowResultBmp = true;
        mDetectingPb.setVisibility(View.VISIBLE);

        mDetectHandler.post(new Runnable() {
            @Override
            public void run() {
                final String detectResult = TessManager.getInstance(getActivity()).detectText(cropBitmap);
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDetectingPb.setVisibility(View.GONE);
                        mDetectResultLayout.setVisibility(View.VISIBLE);
                        mDetectResultTV.setText(detectResult);
                    }
                });
            }
        });

    }

    private Bitmap getBitmap() {
        Drawable drawable = mEditImageView.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(
                mEditImageView.getWidth(),
                mEditImageView.getHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);


        float[] values = new float[9];

        Matrix matrix = mEditImageView.getImageMatrix();
        matrix.getValues(values);

        final float scaleX = values[Matrix.MSCALE_X];
        final float scaleY = values[Matrix.MSCALE_Y];

        int startX = (int)mCropView.getLeftTopPoint().x - (int)(values[2]);
        if (startX < 0) startX = 0;
        int startY = (int)mCropView.getLeftTopPoint().y - (int)(values[5]);
        if (startY < 0) startY = 0;
        int cropWidth = (int)mCropView.getRightTopPoint().x - startX;
        int cropHeight = (int)mCropView.getLeftBottomPoint().y - startY;

        bitmap = Bitmap.createBitmap(mEditBitmap, startX, startY, cropWidth, (int)(cropHeight / scaleY));
        return bitmap;
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onSucceed(Bitmap bitmap) {
        mEditBitmap = bitmap;
        Log.d(TAG, "mEditBitmap.getWidth() : " + mEditBitmap.getWidth() + ", mEditBitmap.getHeight() : " + mEditBitmap.getHeight());
    }

    @Override
    public void onFailed() {

    }
}
