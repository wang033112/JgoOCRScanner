package com.jgo.ocrscanner.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jgo.ocrscanner.R;
import com.jgo.ocrscanner.utils.ScreenUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ke-oh on 2019/08/18.
 *
 */

public class TakePictureFragment extends Fragment implements View.OnClickListener {


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final String CAMERA_THREAD_NAME = "Camera2";
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private boolean mIsSurfaceCreated;

    private SurfaceHolderCallback mSurfaceCallback;
    private CameraManager mCameraManager;
    private Handler childHandler, mainHandler;
    private String mCameraID;
    private ImageReader mImageReader;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;

    private HandlerThread mWorkHandlerThread;
    private OnTakePictureListener mOnTakePictureListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_take_picture, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mSurfaceView
        mSurfaceView = view.findViewById(R.id.camera_surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceCallback = new SurfaceHolderCallback();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mWorkHandlerThread = new HandlerThread(CAMERA_THREAD_NAME);
        mWorkHandlerThread.start();
        childHandler = new Handler(mWorkHandlerThread.getLooper());
        mainHandler = new Handler(getActivity().getMainLooper());
    }

    private void initCamera() {
        mCameraID = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
        mImageReader = ImageReader.newInstance(ScreenUtils.getScreenWidth(getActivity()),
                ScreenUtils.getScreenHeight(getActivity()), ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mCameraDevice.close();
                mSurfaceView.setVisibility(View.GONE);

                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (mOnTakePictureListener != null) {
                    if (bitmap != null) {
                        mOnTakePictureListener.onSucceed(bitmap);
                    } else {
                        mOnTakePictureListener.onFailed();
                    }
                }
            }
        }, mainHandler);
        //
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * preview the camera
     */
    private void takePreview() {
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), captureSessionCallback, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture_bt :
                takePicture();
                break;
        }

    }

    /**
     * take picture
     */
    public void takePicture() {
        if (mCameraDevice == null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mIsSurfaceCreated = true;
            initCamera();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mIsSurfaceCreated = false;
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    }

    /**
     * The callback for camera
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            takePreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(getActivity(), "Failed to open the camera", Toast.LENGTH_LONG).show();
        }
    };

    private CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            if (null == mCameraDevice || mPreviewRequestBuilder == null) return;
            mCameraCaptureSession = cameraCaptureSession;
            try {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                mCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, childHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Toast.makeText(getActivity(), "配置失败", Toast.LENGTH_SHORT).show();
        }
    };


    public void setOnTakePictureListener(OnTakePictureListener onTakePictureListener) {
        this.mOnTakePictureListener = onTakePictureListener;
    }

    public interface OnTakePictureListener {
        void onSucceed(Bitmap bitmap);
        void onFailed();
    }
}
