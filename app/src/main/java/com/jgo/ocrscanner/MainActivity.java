package com.jgo.ocrscanner;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jgo.ocrscanner.fragment.EditPictureFragment;
import com.jgo.ocrscanner.fragment.TakePictureFragment;

public class MainActivity extends Activity implements View.OnClickListener, TakePictureFragment.OnTakePictureListener {

    private static final int TAKE_PIC_MODE = 1;
    private static final int EDIT_PIC_MODE = 2;

    private Button mTakePictureBtn;
    private FrameLayout mTakePictureLayout;
    private FrameLayout mTakePicToolLayout;
    private RelativeLayout mEditPicToolLayout;
    private TakePictureFragment mTakePictureFragment;
    private EditPictureFragment mEditPictureFragment;

    private Button mSaveEditBtn;

    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mTakePictureLayout = findViewById(R.id.camera_surface_layout);
        mTakePictureBtn = findViewById(R.id.take_picture_bt);
        mTakePictureBtn.setOnClickListener(this);

        mTakePicToolLayout = findViewById(R.id.take_layout);
        mEditPicToolLayout = findViewById(R.id.edit_layout);

        mSaveEditBtn = findViewById(R.id.save_edit_btn);
        mSaveEditBtn.setOnClickListener(this);

        mEditPictureFragment = new EditPictureFragment();

        mTakePictureFragment = new TakePictureFragment();
        mTakePictureFragment.setOnTakePictureListener(this);
        setMode(TAKE_PIC_MODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture_bt :
                if (mTakePictureFragment != null) {
                    mTakePictureFragment.takePicture();
                }
                break;

            case R.id.save_edit_btn :
                if (mEditPictureFragment != null) {
                    mEditPictureFragment.cropPicture();
                }
        }

    }


    @Override
    public void onSucceed(Bitmap bitmap) {
        if (mEditPictureFragment != null) {
            mEditPictureFragment.onSucceed(bitmap);
        }

        setMode(EDIT_PIC_MODE);
    }

    @Override
    public void onFailed() {

    }

    private void setMode(int mode) {
        switch (mode) {
            case TAKE_PIC_MODE :
                getFragmentManager().beginTransaction().replace(R.id.camera_surface_layout, mTakePictureFragment, "TakePictureFragment").commit();
                mEditPicToolLayout.setVisibility(View.GONE);
                mTakePicToolLayout.setVisibility(View.VISIBLE);
                break;
            case EDIT_PIC_MODE:
                getFragmentManager().beginTransaction().replace(R.id.camera_surface_layout, mEditPictureFragment, "EditPictureFragment").commit();
                mTakePicToolLayout.setVisibility(View.GONE);
                mEditPicToolLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
