package com.jgo.ocrscanner.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jgo.ocrscanner.R;
import com.jgo.ocrscanner.View.CropView;
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
            mEditImageView.setImageBitmap(mEditBitmap);
        }

        mCropView = view.findViewById(R.id.crop_picture_view);
    }

    public void cropPicture() {

        //getBitmap
        mEditImageView.setImageBitmap(getBitmap());
        mCropView.setVisibility(View.GONE);
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


        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = values[Matrix.MSCALE_X];
        final float scaleY = values[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        //final int origW = mEditImageView.getDrawable().getIntrinsicWidth();
        //final int origH = mEditImageView.getDrawable().getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(mEditBitmap.getWidth() * scaleX);
        final int actH = Math.round(mEditBitmap.getHeight() * scaleY);

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

    private int[] getBitmapOffset(ImageView imageView, boolean includeLayout) {
        int[] offset = new int[2];
        float[] values = new float[9];

        Matrix matrix = imageView.getImageMatrix();
        matrix.getValues(values);

        // x方向上的偏移量(单位px)
        offset[0] = (int) values[2];
        // y方向上的偏移量(单位px)
        offset[1] = (int) values[5];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            int paddingTop = imageView.getPaddingTop();
            int paddingLeft = imageView.getPaddingLeft();

            offset[0] += paddingLeft + params.leftMargin;
            offset[1] += paddingTop + params.topMargin;
        }
        return offset;
    }
}
