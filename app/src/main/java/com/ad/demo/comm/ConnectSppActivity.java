package com.ad.demo.comm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.demo.MainActivity;
import com.ad.demo.R;
import com.ad.demo.base.BaseApplication;
import com.ad.demo.comm.ble.SppDeviceScanActivity;
import com.ad.demo.utils.MessageUtil;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.sio.StatusType;
import com.ad.sio.spp.SioSpp;

/**
 * Created by endyc on 2018-05-21.
 */

public class ConnectSppActivity extends Activity {

    //-------------------define info--------------
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 0;
    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;
    //-------------------base info end------------
    private SioBase mSioBase;
    private TextView mConectButton;
    //-------------------define info end----------
    private OnCommListener onCommListener = new OnCommListener() {

        @Override
        public void onStatus(Object object, StatusEventArg statusEventArg) {
            MessageUtil.List(StatusType.format(statusEventArg.getStatus()) + "--" + statusEventArg.getMsg(), mBaseRes);
            switch (statusEventArg.getStatus()) {
                case StatusType.CONNECT_OK:
                    ((BaseApplication) getApplication()).setSio(mSioBase);
                    startActivity(new Intent().setClass(getApplicationContext(), MainActivity.class));
                    finish();
                    break;
            }
        }

        @Override
        public void onReceived(Object object, byte[] iData) {

        }
    };

    //-------------------base function------------
    private void initUI() {

        mConectButton = (TextView) findViewById(R.id.textview_connect);

        mConectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent().setClass(ConnectSppActivity.this, SppDeviceScanActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });
    }
    //-------------------base function end----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_spp);

        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        this.setTitle(R.string.connect_spp);

        mBaseRes = (TextView) findViewById(R.id.base_res_value);
        try {
            //加载当前通讯类
            mSioBase = new SioSpp();
            //加载当前通讯类状态监听
            mSioBase.setOnCommListener(onCommListener);
        } catch (Exception e) {
            return;
        }

        initUI();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When BtDeviceScanActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getExtras().getString(
                            SppDeviceScanActivity.EXTRAS_DEVICE_NAME);
                    String address = data.getExtras().getString(
                            SppDeviceScanActivity.EXTRA_DEVICE_ADDRESS);
                    Log.d("Bluetooth", name + " -- " + address);

                    BluetoothDevice device = BluetoothAdapter
                            .getDefaultAdapter().getRemoteDevice(address);

                    if (device == null) {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.ad_comm_spp_connect_fail),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mSioBase.connect(address, 0);
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.ad_comm_spp_connecting),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent().setClass(getApplicationContext(), ConnectBaseActivity.class));
            //finish();
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