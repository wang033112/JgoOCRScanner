package com.jgo.ocrscanner.tess;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.jgo.ocrscanner.utils.JgoLogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 *
 */
public class TessManager {

    static final String TAG = TessManager.class.getName();

    private static TessManager mInstance;
    private TessBaseAPI mTessBaseAPI;

    private TessManager(Context context){
        TessDataManager.initTessTrainedData(context);
        mTessBaseAPI = new TessBaseAPI();
        //mTessBaseAPI.setDebug(true);
        mTessBaseAPI.init(TessDataManager.getTesseractFolder(), "jpn");
        mTessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
    }

    public static TessManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TessManager(context);
        }
        return mInstance;
    }

    /**
     *
     * @param bitmap
     * @return
     */
    public String detectText(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }

        String fileName = String.valueOf(System.currentTimeMillis());
        //saveBitmapToSD(TessDataManager.getTesseractFolder(), fileName, bitmap);
        mTessBaseAPI.setImage(bitmap);
        String inspection = mTessBaseAPI.getUTF8Text();
        JgoLogUtil.d(TAG, fileName + " - " + inspection);
        System.gc();
        return inspection;
    }

    private void saveBitmapToSD(String path, String fileName, Bitmap bt) {
        File file = new File(path, fileName + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bt.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
