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

        int amount = 0;
        int count = 0;
        Bitmap convertMap = targetMap.copy(Bitmap.Config.ARGB_8888, true);
        int[][] alphas = new int[width][height];
        int[][] colors = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int col = convertMap.getPixel(i, j);
                int alpha = col & 0xFF000000;

                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);

                //X = 0.3×R+0.59×G+0.11×B
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                colors[i][j] = gray;
                alphas[i][j] = alpha;
                amount += gray;
                count ++;

            }
        }

        if (amount > 0 && count > 0) {
            int average = amount / count;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int col = colors[i][j];
                    if (col <= average) {
                        col = 0;
                    } else {
                        col = 255;
                    }
                    int newColor = alphas[i][j] | (col << 16) | (col << 8) | col;
                    convertMap.setPixel(i, j, newColor);
                }
            }
        }

        return convertMap;
    }
}
