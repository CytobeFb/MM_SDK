package com.ad.yeyoo.comm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.yeyoo.MainActivity;
import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.sio.StatusType;
import com.ad.sio.usb.SioHid;

/**
 * Created by endyc on 2018-05-21.
 */

public class ConnectHidActivity extends Activity {

    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;

    private SioBase mSioBase;
    //-------------------base info end------------

    //-------------------define info--------------
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
            MessageUtil.List(SioBase.ByteArrayToHexString(iData, 0, iData.length), mBaseRes);
        }
    };

    //-------------------base function------------
    private void initUI() {

        mConectButton = (TextView) findViewById(R.id.textview_connect);

        mConectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mSioBase.connect("AD0811-3", 0);
            }
        });
    }
    //-------------------base function end----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_hid);

        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        this.setTitle(R.string.connect_hid);

        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        try {
            //?????????????????????
            mSioBase = new SioHid();
            mSioBase.setContext(mContext);
            //?????????????????????????????????
            mSioBase.setOnCommListener(onCommListener);
        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.usb_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        initUI();
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