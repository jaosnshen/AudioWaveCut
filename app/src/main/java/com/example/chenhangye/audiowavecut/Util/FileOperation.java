package com.example.chenhangye.audiowavecut.Util;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by chenhangye on 2016/10/6.
 */

public class FileOperation {
    public static String getPath(){
        String outputPath= Environment.getExternalStorageDirectory()+"/0"+
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(System
                        .currentTimeMillis()) + ".pcm";
        return outputPath;
    }
}
