package com.reeman.basebigman.util;

import android.os.Looper;
import android.util.Log;

/**
 * <p>文件描述：判断方法是否运行在主线程中<p>
 * <p>作者：Mr Yang<p>
 * <p>邮箱：yyr@jmslam.com<p>
 * <p>创建时间：2018/9/18<p>
 * <p>更改时间：2018/9/18<p>
 */

public class ThreadUtils {

    public static final String TAG = "ThreadUtils";

    public static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        Log.i(TAG, "isInMainThread myLooper=" + myLooper + ";mainLooper=" + mainLooper);
        return myLooper == mainLooper;
    }

}
