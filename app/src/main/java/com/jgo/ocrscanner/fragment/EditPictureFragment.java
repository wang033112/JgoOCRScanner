package com.jgo.ocrscanner.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

/**
 * Created by ke-oh on 2019/08/18.
 *
 */

public class EditPictureFragment extends Fragment implements View.OnClickListener, TakePictureFragment.OnTakePictureListener {

    private static final String TAG = EditPictureFragment.class.getSimpleName();

    private Bitmap mEditBitmap;
    private ImageView mEditImageView;

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
    }

    public void cropPicture() {
        Log.d(TAG, "mEditImageView.getWidth() : " + mEditImageView.getWidth() + ", mEditImageView.getHeight() : " + mEditImageView.getHeight());
        //Bitmap bitmap = ((BitmapDrawable)mEditImageView.getDrawable()).getBitmap();
        //bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        //Log.d(TAG, "bitmap.getWidth() : " + bitmap.getWidth() + ", bitmap.getHeight() : " + bitmap.getHeight());

        //getBitmap
        mEditImageView.setImageBitmap(getBitmap());
        //mEditBitmap = Bitmap.createBitmap(mEditBitmap, mEditBitmap.getWidth() / 2, mEditBitmap.getHeight() / 2, mEditBitmap.getWidth() / 2, mEditBitmap.getHeight() / 2);
        //mEditImageView.setImageBitmap(mEditBitmap);
    }

    private Bitmap getBitmap() {
        Drawable drawable = mEditImageView.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(
                //drawable.getIntrinsicWidth(),
                mEditImageView.getWidth(),
                mEditImageView.getMaxHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        bitmap = Bitmap.createBitmap(mEditBitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
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
