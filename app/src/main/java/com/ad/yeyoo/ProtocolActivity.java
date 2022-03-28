package com.ad.yeyoo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.utils.ConverterUtil;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.WakeLockUtil;
import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolEventArg;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;

public class ProtocolActivity extends Activity {

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
    //-------------------define info--------------
    private EditText mAddress;
    private EditText mCid;
    private EditText mCid2;
    private EditText mData;
    private TextView mPacketButton;
    private EditText mSendData;
    private TextView mRecvData;
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            final ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            MessageUtil.List(ConverterUtil.ByteArrayToHexString(psData.toArray()), mBaseRes);
            mRecvData.post(new Runnable() {
                @Override
                public void run() {
                    mRecvData.setText(ConverterUtil.ByteArrayToHexString(psData.toArray()));
                }
            });
        }
    };
    private TextView mSendButton;
    private byte[] mSendBuff;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.protocol_data_packet:
                    try {
                        byte[] b_address = ConverterUtil.HexStringToBytes(mAddress.getText().toString());
                        byte[] b_cid = ConverterUtil.HexStringToBytes(mCid.getText().toString());
                        byte[] b_cid2 = ConverterUtil.HexStringToBytes(mCid2.getText().toString());
                        byte b_len = (byte) (mData.getText().toString().length() / 2);
                        byte[] b_data = null;
                        if (b_len > 0) {
                            b_data = ConverterUtil.HexStringToBytes(mData.getText().toString());
                        }


                        int m_address = (b_address[1] & 0xff) * 256 + (b_address[0] & 0xff);
                        byte m_cid = b_cid[0];
                        byte m_cid2 = b_cid2[0];

                        mSendBuff = RcpBase.buildCmdPacketByte(m_address, m_cid, m_cid2, b_data);

                        mSendData.setText(ConverterUtil.ByteArrayToHexString(mSendBuff));
                    } catch (Exception ex) {
                        MessageUtil.List("unknown error", mBaseRes);
                    }


                    break;
                case R.id.protocol_send_button:
                    mRecvData.setText("");
                    mSendBuff = ConverterUtil.HexStringToBytes(mSendData.getText().toString());
                    mSioBase.send(mSendBuff);
                    break;
            }
        }

        ;
    };
    //-------------------define info end----------

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

    //-------------------base function end----------

    //-------------------base function------------
    protected void initUI() {
        mAddress = (EditText) findViewById(R.id.protocol_addr_value);
        mCid = (EditText) findViewById(R.id.protocol_cid_value);
        mCid2 = (EditText) findViewById(R.id.protocol_cid2_value);
        mData = (EditText) findViewById(R.id.protocol_data_value);
        mPacketButton = (TextView) findViewById(R.id.protocol_data_packet);

        mSendData = (EditText) findViewById(R.id.protocol_send_value);
        mRecvData = (TextView) findViewById(R.id.protocol_recv_value);
        mSendButton = (TextView) findViewById(R.id.protocol_send_button);

        mPacketButton.setOnClickListener(onClickListener);
        mSendButton.setOnClickListener(onClickListener);

        mAddress.setText("FFFF");
        mCid.setText("82");
        mCid2.setText("32");
        mData.setText("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_protocol);
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.page_test_name);
        WakeLockUtil.KeepScreenOn(this);//高亮屏幕,禁止锁屏

        baseinfo_init();

        initUI();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
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
