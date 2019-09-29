package com.jgo.ocrscanner.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

/**
 * ScreenUtils
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * get screen width
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * get screen height
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int px2dip(int pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    /**
     * Black-White
     *
     * @param targetMap
     * @return
     */
    public static Bitmap gray2Binary(Bitmap targetMap) {
        int width = targetMap.getWidth();
        int height = targetMap.getHeight();

        Bitmap convertMap = targetMap.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int col = convertMap.getPixel(i, j);
                int alpha = col & 0xFF000000;

                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);

                //X = 0.3×R+0.59×G+0.11×B
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);

                if (gray <= 127) {
                    gray = 0;
                } else {
                    gray = 255;
                }

                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                convertMap.setPixel(i, j, newColor);
            }
        }
        return convertMap;
    }
}
