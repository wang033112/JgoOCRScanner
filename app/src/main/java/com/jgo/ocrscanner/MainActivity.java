package com.jgo.ocrscanner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jgo.ocrscanner.fragment.EditPictureFragment;
import com.jgo.ocrscanner.fragment.TakePictureFragment;

public class MainActivity extends Activity implements View.OnClickListener, TakePictureFragment.OnTakePictureListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TAKE_PIC_MODE = 1;
    private static final int EDIT_PIC_MODE = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 999;
    private static final int CROP_IMAGE_RESULT = 1;

    private Button mTakePictureBtn;
    private FrameLayout mTakePictureLayout;
    private FrameLayout mTakePicToolLayout;
    private RelativeLayout mEditPicToolLayout;
    private TakePictureFragment mTakePictureFragment;
    private EditPictureFragment mEditPictureFragment;

    private Button mSaveEditBtn;
    private Button mCancelBtn;

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

        mCancelBtn = findViewById(R.id.cancel_btn);
        mCancelBtn.setOnClickListener(this);

        mEditPictureFragment = new EditPictureFragment();

        if (checkAndRquestCameraPermission()) {
            initCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Init the camera
     */
    private void initCamera() {
        mTakePictureFragment = new TakePictureFragment();
        mTakePictureFragment.setOnTakePictureListener(this);
        setMode(TAKE_PIC_MODE);
    }

    private boolean checkAndRquestCameraPermission() {

        boolean granted = false;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                granted = true;
            }
        } else {
            //TODO
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied", Toast.LENGTH_SHORT).show();
                } else {
                    initCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                break;
            case R.id.cancel_btn:
                if (mEditPictureFragment != null) {
                    mEditPictureFragment.resetEdit();
                }
                setMode(TAKE_PIC_MODE);
                break;
        }

    }


    @Override
    public void onSucceed(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
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
