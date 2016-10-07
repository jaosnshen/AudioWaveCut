package com.example.chenhangye.audiowavecut.Util;

import java.sql.Timestamp;

/**
 * Created by chenhangye on 2016/10/2.
 */

public class TimeUtils {
    /**
     * 把毫秒，转换成 ：, 如：03:10
     * @param millisencond
     * @return
     */
    public static String convertMilliSecondToMinute(int millisencond) {
        int oneMinute = 1000 * 60;
        int minutes = millisencond / oneMinute;
        int sencond = (millisencond - minutes * oneMinute) / 1000;
        return getNum(minutes) + ":" + getNum(sencond);
    }

    /**
     * 把秒，转换成 ：, 如：03:10
     * @param seconds
     * @return
     */
    public static String convertSecondToMinute(int seconds) {
        int oneMinute = 60;
        int minutes = seconds / oneMinute;
        int mSecond = seconds - minutes * oneMinute;
        return getNum(minutes) + ":" + getNum(mSecond);
    }

    private static String getNum(int num) {
        if (num >= 10) {
            return "" + num;
        } else {
            return "0" + num;
        }
    }

    public static String getTimestamp() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return String.valueOf(ts.getTime());
    }
}
