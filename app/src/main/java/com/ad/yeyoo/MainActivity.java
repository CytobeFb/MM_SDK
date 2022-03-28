package com.ad.yeyoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.business.BusinessMainActivity;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.PreferenceUtil;
import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolEventArg;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.sio.StatusType;
import com.ad.sio.com.SioCom;

/**
 * Created by endyc on 2018-06-19.
 */

public class MainActivity extends Activity {

    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;

    private SioBase mSioBase;
    private SioBase mSioBase2;
    private RcpBase mRcpBase;
    private int mPosPort = -1;
    private int mPosBaudrate = 0;
    private OnCommListener onCommListener = new OnCommListener() {

        @Override
        public void onStatus(Object object, StatusEventArg statusEventArg) {
            MessageUtil.List(StatusType.format(statusEventArg.getStatus()) + "--" + statusEventArg.getMsg(), mBaseRes);
            switch (statusEventArg.getStatus()) {
                case StatusType.CONNECT_OK:
                    ((BaseApplication) getApplication()).setSio(mSioBase2);
                    baseinfo_init();
                    Command(RcpBase.RCP_CMD_INFO, RcpBase.RCP_MSG_GET);
                    break;
            }
        }

        @Override
        public void onReceived(Object object, byte[] iData) {

            mRcpBase.receivePacketByte(iData);
        }
    };
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            switch (psData.Code) {
                case RcpBase.RCP_CMD_INFO:
                    MessageUtil.List("Code: " + mRcpBase.getCode() + "\r\n" +
                                    "Type: " + mRcpBase.getType() + "\r\n" +
                                    "Mode: " + mRcpBase.getMode() + "\r\n" +
                                    "Version: " + mRcpBase.getVersion() + "\r\n" +
                                    "Address: " + mRcpBase.getAddress() + "\r\n"
                            , mBaseRes);


                    if (!mRcpBase.getMode().contains("M") && !mRcpBase.getMode().contains("Q")) {
                        MessageUtil.List("读卡器与SDK不匹配,请联系厂家", mBaseRes);
                    } else {
                        startActivity(new Intent().setClass(getApplicationContext(), BusinessMainActivity.class));
                    }
                    break;
            }
        }
    };
    private TextView mMainConnect;
    private TextView mMainEnter;
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_connect_name:
                    //Get base information-获取基本信息
//                    Command(RcpBase.RCP_CMD_INFO, RcpBase.RCP_MSG_GET);
                    mSioBase2 = new SioCom();
                    mSioBase2.setOnCommListener(onCommListener);
                    mSioBase2.connect(PreferenceUtil.getPrefString(mContext, "comportName", "/dev/ttyS3"),
                            PreferenceUtil.getPrefInt(mContext, "baudrateName", 57600));
                    break;
            }
        }

        ;
    };

    //-------------------base info end------------

    private void baseinfo_init() {

        try {
            mRcpBase = ((BaseApplication) getApplication()).getRcp();
            mRcpBase.setOnProtocolListener(onProtocolListener);

            mSioBase = ((BaseApplication) getApplication()).getSio();
            mSioBase.setContext(mContext);
            mSioBase.setOnCommListener(onCommListener);
        } catch (Exception e) {
            return;
        }
    }

    private void Command(byte nCode, byte nType) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType));
    }

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.title_version);

        mMainConnect = findViewById(R.id.main_connect_name);
        mMainEnter = findViewById(R.id.main_enter_name);
        mMainConnect.setOnClickListener(onClickListener);
        mMainEnter.setOnClickListener(onClickListener);
//        baseinfo_init();
    }

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