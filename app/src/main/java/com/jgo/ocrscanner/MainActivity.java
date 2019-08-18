package com.jgo.ocrscanner;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jgo.ocrscanner.fragment.EditPictureFragment;
import com.jgo.ocrscanner.fragment.TakePictureFragment;

public class MainActivity extends Activity implements View.OnClickListener, TakePictureFragment.OnTakePictureListener {

    private static final int TAKE_PIC_MODE = 1;
    private static final int EDIT_PIC_MODE = 2;

    private Button mTakePicBtn;
    private FrameLayout mTakePictureLayout;
    private TakePictureFragment mTakePictureFragment;
    private EditPictureFragment mEditPictureFragment;

    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mTakePicBtn = findViewById(R.id.take_picture_bt);
        mTakePicBtn.setOnClickListener(this);

        mTakePictureLayout = findViewById(R.id.camera_surface_layout);

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
                break;
            case EDIT_PIC_MODE:
                getFragmentManager().beginTransaction().replace(R.id.camera_surface_layout, mEditPictureFragment, "EditPictureFragment").commit();
                break;
        }
    }
}
