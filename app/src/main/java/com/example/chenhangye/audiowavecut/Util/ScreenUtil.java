package com.example.chenhangye.audiowavecut.Util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by chenhangye on 2016/10/5.
 */

public class ScreenUtil {


    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidthPix(Context context) {
        WindowManager wm = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

}
