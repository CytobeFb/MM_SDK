package com.ad.yeyoo.mm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.WakeLockUtil;
import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolEventArg;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;


/**
 * Created by endyc on 2018-06-19.
 */

public class MMParaActivity extends Activity {

    //-------------------define function-----------
    byte[] params = new byte[23];
    byte[] params_outcard = new byte[23];
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
    private Spinner mCommTypeSpinner;
    private Spinner mWorkTypeSpinner;

    //-------------------base info end------------

    //-------------------define info--------------
    private Spinner mReadtypeSpinner;
    private EditText mReadIntervalEdit;
    private EditText mReadDelayEdit;
    private EditText mSameidEdit;
    private Spinner mBeepSpinner;
    private TextView mBaseGetButton;
    private TextView mBaseSetButton;
    private Spinner mOutCardSpinner;
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            switch (psData.Code) {
                case RcpMM.RCP_MM_PARA:
                    if (psData.Type == 0 && psData.Length > 0) {
                        setParameters(psData.Payload, psData.Length);
                        MessageUtil.Show("Get para succeed!", mBaseRes);
                    } else if (psData.Type == 0) {
                        MessageUtil.Show("Set para succeed!", mBaseRes);
                    }
                    break;
                case RcpMM.RCP_MM_OUTCARD:
                    if (psData.Type == 0 && psData.Length > 0) {
                        setOutcardParameters(psData.Payload, psData.Length);
                        MessageUtil.Show("Get Outcard succeed!", mBaseRes);
                    } else if (psData.Type == 0) {
                        MessageUtil.Show("Set Outcard succeed!", mBaseRes);
                    }
                    break;
            }
        }
    };
    private TextView mOutCardGetButton;
    private TextView mOutCardSetButton;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.para_base_get:
                    Command(RcpMM.RCP_MM_PARA, RcpBase.RCP_MSG_GET);
                    break;
                case R.id.para_base_set:
                    params[0] = (byte) mCommTypeSpinner.getSelectedItemPosition();
                    params[1] = (byte) mWorkTypeSpinner.getSelectedItemPosition();
                    params[2] = (byte) mReadtypeSpinner.getSelectedItemPosition();
                    params[3] = (byte) Integer.parseInt(mReadIntervalEdit.getText().toString());
                    params[4] = (byte) Integer.parseInt(mReadDelayEdit.getText().toString());
                    int sameid = Integer.parseInt(mSameidEdit.getText().toString());
                    params[9] = (byte) (sameid / 256);
                    params[10] = (byte) (sameid % 256);
                    params[11] = (byte) mBeepSpinner.getSelectedItemPosition();
                    Command(RcpMM.RCP_MM_PARA, RcpBase.RCP_MSG_SET, params);
                    break;
                case R.id.para_outcard_get:
                    Command(RcpMM.RCP_MM_OUTCARD, RcpBase.RCP_MSG_GET);
                    break;
                case R.id.para_outcard_set:
                    params_outcard[0] = (byte) mOutCardSpinner.getSelectedItemPosition();
                    Command(RcpMM.RCP_MM_OUTCARD, RcpBase.RCP_MSG_SET, params_outcard);
                    break;
            }
        }
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

    private void Command(byte nCode, byte nType) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType));
    }

    //-------------------base event-----------------

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    //-------------------base function------------
    private void initUI() {


        mCommTypeSpinner = (Spinner) findViewById(R.id.para_commtype_value);
        mWorkTypeSpinner = (Spinner) findViewById(R.id.para_workmode_value);
        mReadtypeSpinner = (Spinner) findViewById(R.id.para_readtype_value);
        mReadIntervalEdit = (EditText) findViewById(R.id.para_interval_value);
        mReadDelayEdit = (EditText) findViewById(R.id.para_delay_value);
        mSameidEdit = (EditText) findViewById(R.id.para_sameid_value);
        mBeepSpinner = (Spinner) findViewById(R.id.para_beep_value);
        mBaseGetButton = (TextView) findViewById(R.id.para_base_get);
        mBaseSetButton = (TextView) findViewById(R.id.para_base_set);

        mOutCardSpinner = (Spinner) findViewById(R.id.para_outcard_value);
        mOutCardGetButton = (TextView) findViewById(R.id.para_outcard_get);
        mOutCardSetButton = (TextView) findViewById(R.id.para_outcard_set);

        mBaseGetButton.setOnClickListener(onClickListener);
        mBaseSetButton.setOnClickListener(onClickListener);

        mOutCardGetButton.setOnClickListener(onClickListener);
        mOutCardSetButton.setOnClickListener(onClickListener);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    Command(RcpMM.RCP_MM_PARA, RcpBase.RCP_MSG_GET);
                    Thread.sleep(40);
                    Command(RcpMM.RCP_MM_OUTCARD, RcpBase.RCP_MSG_GET);
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    //-------------------base function end----------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mm_para);    //需要修改
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.title_base);
        WakeLockUtil.KeepScreenOn(this);//禁止锁屏

        baseinfo_init();

        initUI();
    }
    //-------------------base event end-------------

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

    public void setParameters(final byte[] datArray, final byte datalen) {
        mCommTypeSpinner.post(new Runnable() {
            public void run() {
                try {
                    mCommTypeSpinner.setSelection((datArray[0] & 0xff), true);
                    mWorkTypeSpinner.setSelection((datArray[1] & 0xff), true);
                    mReadtypeSpinner.setSelection((datArray[2] & 0xff), true);
                    mReadIntervalEdit.setText(String.valueOf((datArray[3] & 0xff)));
                    mReadDelayEdit.setText(String.valueOf((datArray[4] & 0xff)));
                    int samedid = (datArray[9] & 0xff) * 256 + (datArray[10] & 0xff);
                    mSameidEdit.setText(String.valueOf(samedid));
                    mBeepSpinner.setSelection((datArray[11] & 0xff), true);

                    params = new byte[datalen];
                    for (int i = 0; i < datalen; i++) {
                        params[i] = datArray[i];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOutcardParameters(final byte[] datArray, final byte datalen) {
        mCommTypeSpinner.post(new Runnable() {
            public void run() {
                try {
                    mOutCardSpinner.setSelection((datArray[0] & 0xff), true);
                    params_outcard = new byte[datalen];
                    for (int i = 0; i < datalen; i++) {
                        params_outcard[i] = datArray[i];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //-------------------define function end--------
}
