package com.ad.yeyoo.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.yeyoo.MainActivity;
import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.PreferenceUtil;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.sio.StatusType;
import com.ad.sio.net.SioNetClient;

/**
 * Created by endyc on 2018-05-21.
 */

@SuppressLint("HandlerLeak")
public class ConnectTcpActivity extends Activity {

    //-------------------define info--------------
    private static final String HOSTNAME_REGEXP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;
    private SioBase mSioBase;
    protected OnCommListener onCommListener = new OnCommListener() {

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
    //-------------------base info end------------
    private String m_IPAddress = "192.168.2.116";
    private String m_IPPort = "49152";
    private EditText mConectTcpIpAddress;
    private EditText mConectTcpIpPort;
    //-------------------define info end----------
    private TextView mConectButton;

    //-------------------base function------------
    protected void initUI() {

        mConectTcpIpAddress = (EditText) findViewById(R.id.connect_tcp_ip_address_text);
        mConectTcpIpPort = (EditText) findViewById(R.id.connect_tcp_ip_port_text);
        mConectButton = (TextView) findViewById(R.id.textview_connect);

        m_IPAddress = PreferenceUtil.getPrefString(mContext, "ipaddress", m_IPAddress);
        m_IPPort = PreferenceUtil.getPrefString(mContext, "ipport", m_IPPort);

        mConectTcpIpAddress.setText(m_IPAddress);
        mConectTcpIpPort.setText(m_IPPort);

        mConectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String host = mConectTcpIpAddress.getText().toString();
                String strPort = mConectTcpIpPort.getText().toString();

                if (host == null || host.length() <= 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.ip_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (strPort == null || strPort.length() <= 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.port_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                int port = Integer.parseInt(strPort);

                if (host.matches(HOSTNAME_REGEXP))
                    ;
                else {
                    Toast.makeText(mContext, getResources().getString(R.string.ip_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                PreferenceUtil.setPrefString(mContext, "ipaddress", host);
                PreferenceUtil.setPrefString(mContext, "ipport", strPort);

                mSioBase.connect(host, port);
            }
        });
    }
    //-------------------base function end----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_tcp);

        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        this.setTitle(R.string.connect_tcp);

        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        try {
            //加载当前通讯类
            mSioBase = new SioNetClient();
            //加载当前通讯类状态监听
            mSioBase.setOnCommListener(onCommListener);
        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.tcp_error),
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
