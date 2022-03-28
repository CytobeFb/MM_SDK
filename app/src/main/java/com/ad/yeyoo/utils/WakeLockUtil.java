package com.ad.yeyoo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

/**
 * Created by endyc on 2019-07-01.
 */

public class WakeLockUtil {

    private static PowerManager.WakeLock mWakeLock;

    public static void acquire(Context context) {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    context.getClass().getCanonicalName());
            mWakeLock.acquire();
        }
    }

    public static void release() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    /**
     * 保持屏幕高亮,禁止锁屏
     *
     * @param activity
     */
    public static void KeepScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
    }

    /**
     * 解除屏幕高亮,禁止锁屏
     *
     * @param activity
     */
    public static void KeepScreenOff(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //解除屏幕高亮
    }
}
