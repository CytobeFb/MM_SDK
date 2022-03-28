package com.ad.demo.comm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ad.demo.R;
import com.ad.demo.base.BaseApplication;


public class ConnectBaseActivity extends Activity {

    private TextView mConectRs232;
    private TextView mConectTcpIp;
    private TextView mConnectSpp;
    private TextView mConnectBle;
    private TextView mConnectHid;
    private TextView mConnectCdc;
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textview_connect_rs232:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectRs232Activity.class));
                    break;
                case R.id.textview_connect_hid:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectHidActivity.class));
                    break;
                case R.id.textview_connect_tcp:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectTcpActivity.class));
                    break;
                case R.id.textview_connect_spp:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectSppActivity.class));
                    break;
                case R.id.textview_connect_ble:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectBleActivity.class));
                    break;
                case R.id.textview_connect_cdc:
                    startActivity(new Intent().setClass(getApplicationContext(), ConnectCdcActivity.class));
                    break;
            }

            finish();
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_base);

        ((BaseApplication) getApplication()).addActivity(this);

        mConectRs232 = findViewById(R.id.textview_connect_rs232);
        mConectTcpIp = findViewById(R.id.textview_connect_tcp);
        mConnectSpp = findViewById(R.id.textview_connect_spp);
        mConnectBle = findViewById(R.id.textview_connect_ble);
        mConnectHid = findViewById(R.id.textview_connect_hid);
        mConnectCdc = findViewById(R.id.textview_connect_cdc);

        mConectRs232.setOnClickListener(onClickListener);
        mConectTcpIp.setOnClickListener(onClickListener);
        mConnectSpp.setOnClickListener(onClickListener);
        mConnectBle.setOnClickListener(onClickListener);
        mConnectHid.setOnClickListener(onClickListener);
        mConnectCdc.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getApplication().onTerminate();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}