package com.ad.demo.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.demo.MainActivity;
import com.ad.demo.R;
import com.ad.demo.base.BaseApplication;
import com.ad.demo.utils.MessageUtil;
import com.ad.demo.utils.PreferenceUtil;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.sio.StatusType;
import com.ad.sio.com.SioCom;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class ConnectRs232Activity extends Activity {

    String[] mPortArray = null;
    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;
    //-------------------base info end------------
    private SioBase mSioBase;
    //-------------------define info--------------
    private TextView mConectButton;
    private Spinner mPortSpinner;
    private Spinner mBaudSpinner;
    private List<String> mPortList = new ArrayList<String>();
    private int mPosPort = -1;
    private int mPosBaudrate = 0;
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

        mPosPort = PreferenceUtil.getPrefInt(mContext, "comport", mPosPort);
        mPosBaudrate = PreferenceUtil.getPrefInt(mContext, "baudrate", mPosBaudrate);

        mConectButton = (TextView) findViewById(R.id.textview_connect);
        mPortSpinner = (Spinner) findViewById(R.id.connect_rs232_comport_value);
        mBaudSpinner = (Spinner) findViewById(R.id.connect_rs232_baudrate_value);

        //mBaudSpinner.setSelection(3);
        mBaudSpinner.setSelection(mPosBaudrate);

        mConectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mPosPort = mPortSpinner.getSelectedItemPosition();
                mPosBaudrate = mBaudSpinner.getSelectedItemPosition();
                if (mPosPort < 0) {
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.rs232_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(
                        getApplicationContext(),
                        ((SioCom) mSioBase).getPortName(mPortSpinner.getSelectedItemPosition()),
                        Toast.LENGTH_SHORT).show();

                PreferenceUtil.setPrefInt(mContext, "comport", mPosPort);
                PreferenceUtil.setPrefInt(mContext, "baudrate", mPosBaudrate);
                PreferenceUtil.setPrefString(mContext, "comportName", ((SioCom) mSioBase).getPortName(mPortSpinner.getSelectedItemPosition()));
                PreferenceUtil.setPrefInt(mContext, "baudrateName", Integer.parseInt(mBaudSpinner.getSelectedItem().toString()));

                mSioBase.connect(((SioCom) mSioBase).getPortName(mPortSpinner.getSelectedItemPosition()),
                        Integer.parseInt(mBaudSpinner.getSelectedItem().toString()));

            }
        });

        mPortArray = ((SioCom) mSioBase).getPortList();
        if (mPortArray != null && mPortArray.length > 0) {
            for (int i = 0; i < mPortArray.length; i++) {
                mPortList.add(mPortArray[i]);
                if (mPortArray[i].contains("ttyMT0")) mPosPort = i;
            }
        } else {
            mPortList = new ArrayList<String>();
            mPortList.add(getResources().getString(R.string.rs232_noport));
            mPosPort = 0;
            mConectButton.setEnabled(false);
        }
        //实例化适配器,里面的参数可以参照UI大全那部分
        ArrayAdapter<String> adapter_port = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_display_style, R.id.txtvwSpinner, mPortList);
        //这个是设置选项卡的显示格式的
        adapter_port.setDropDownViewResource(R.layout.spinner_dropdown_style);
        //加载适配器
        mPortSpinner.setAdapter(adapter_port);
        mPortSpinner.setSelection(mPosPort);
    }
    //-------------------base function end----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_rs232);

        //必须加载
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        this.setTitle(R.string.connect_com);

        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        try {
            //加载当前通讯类
            mSioBase = new SioCom();
            //加载当前通讯类状态监听
            mSioBase.setOnCommListener(onCommListener);
        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.rs232_error),
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
