package com.test.testh264sender;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DecimalFormat;

public class Utils {

    /**
     * 判断wifi是否可用
     * @return true wifi可用；false wifi未连接不可用
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }


    /**
     * byte convert
     *
     * @param size like 3232332
     * @return like 3.23M
     */
    public static String getFormatSize(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        float f;
        if (size >= 1024 * 1024 * 1024) {
            f = ((float) size / (float) (1024 * 1024 * 1024));
            return (df.format(Float.valueOf(f).doubleValue()) + "GB");
        } else if (size >= 1024 * 1024) {
            f = ((float) size / (float) (1024 * 1024));
            return (df.format(Float.valueOf(f).doubleValue()) + "MB");
        } else if (size >= 1024) {
            f = ((float) size / (float) 1024);
            return (df.format(Float.valueOf(f).doubleValue()) + "KB");
        } else {
            return String.valueOf((int) size) + "B";
        }
    }

}
