package com.ad.yeyoo.mm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.yeyoo.ProtocolActivity;
import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.base.ExitAgainClass;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.WakeLockUtil;
import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolEventArg;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;

/**
 * Created by endyc on 2019-05-30.
 */

public class MMMainActivity extends Activity {

    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;

    private SioBase mSioBase;
    private RcpBase mRcpBase;
    private OnCommListener onCommListener = new OnCommListener() {

        @Override
        public void onStatus(Object object, StatusEventArg statusEventArg) {

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
                    break;
            }
        }
    };
    private ExitAgainClass exitActivity = new ExitAgainClass();
    //-------------------define info--------------
    private TextView mDemo;
    private TextView mEPC;
    //-------------------base info end------------
    private TextView mTest;
    private TextView mBasePara;
    private TextView mSeniorPara;
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_demo:
                    startActivity(new Intent().setClass(getApplicationContext(), MMDemoActivity.class));
                    break;
                case R.id.tv_iso6c:
                    startActivity(new Intent().setClass(getApplicationContext(), MMISO6CActivity.class));
                    break;
                case R.id.tv_basepara:
                    startActivity(new Intent().setClass(getApplicationContext(), MMParaActivity.class));
                    break;
                case R.id.tv_seniorpara:
                    startActivity(new Intent().setClass(getApplicationContext(), MMSeniorActivity.class));
                    break;
                case R.id.tv_test:
                    startActivity(new Intent().setClass(getApplicationContext(), ProtocolActivity.class));
                    break;
            }
        }

        ;
    };

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

    //-------------------define info end----------

    private void Command(byte nCode, byte nType) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType));
    }

    //-------------------base function end----------

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    //-------------------base function------------
    protected void initUI() {
        mDemo = (TextView) findViewById(R.id.tv_demo);
        mEPC = (TextView) findViewById(R.id.tv_iso6c);
        mTest = (TextView) findViewById(R.id.tv_test);
        mBasePara = (TextView) findViewById(R.id.tv_basepara);
        mSeniorPara = (TextView) findViewById(R.id.tv_seniorpara);

        mDemo.setOnClickListener(onClickListener);
        mEPC.setOnClickListener(onClickListener);
        mTest.setOnClickListener(onClickListener);
        mBasePara.setOnClickListener(onClickListener);
        mSeniorPara.setOnClickListener(onClickListener);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //Get base information-获取基本信息
                    Command(RcpBase.RCP_CMD_INFO, RcpBase.RCP_MSG_GET);
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_main);
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.page_mm);
        WakeLockUtil.KeepScreenOn(this);//高亮屏幕,禁止锁屏


        baseinfo_init();

        initUI();

    }

    /**
     * 按两次回退键退出程序
     */
    private void pressAgainExit() {
        if (exitActivity.isExit()) {
            WakeLockUtil.KeepScreenOff(this);
            getApplication().onTerminate();
        } else {
            Toast.makeText(mContext, "再按一次退出程序!", Toast.LENGTH_SHORT).show();
            exitActivity.doExitInOneSecond();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            pressAgainExit();
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
