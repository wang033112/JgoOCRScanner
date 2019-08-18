package com.jgo.ocrscanner.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onSucceed(Bitmap bitmap) {
        mEditBitmap = bitmap;
    }

    @Override
    public void onFailed() {

    }
}
