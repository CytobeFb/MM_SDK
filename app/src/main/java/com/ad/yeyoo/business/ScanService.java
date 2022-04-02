package com.ad.yeyoo.business;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class ScanService extends AccessibilityService {
    final static String TAG = "ScanService";
    private ScanGun mScanGun = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub

    }

    private StringBuilder stringBuilder = new StringBuilder();
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode <= 6) {
                return false;
            }
            if (mScanGun.isMaybeScanning(keyCode, event)) {
                return true;
            }

        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onCreate() {
        mScanGun = new ScanGun(new ScanGun.ScanGunCallBack() {

            @Override
            public void onScanFinish(String scanResult) {
                Log.e("11111","1");
                if (!TextUtils.isEmpty(scanResult)) {

                    Toast.makeText(ScanService.this.getBaseContext(),
                            "监听扫描枪数据:" + scanResult, Toast.LENGTH_SHORT).show();
                }
            }
        });

        super.onCreate();
    }




}