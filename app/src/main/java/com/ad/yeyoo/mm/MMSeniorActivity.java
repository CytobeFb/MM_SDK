package com.ad.yeyoo.mm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by endyc on 2018-06-19.
 */

public class MMSeniorActivity extends Activity {

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
    private TextView mResetButton;
    private TextView mEraseButton;
    private TextView mUpdateButton;
    private CheckBox mFreqhoppingCheck;

    //-------------------base info end------------
    private TextView mHoppsettingsGetButton;
    private TextView mHoppsettingsSetButton;
    private Spinner mRegionSpinner;
    private TextView mRegionGetButton;
    private TextView mRegionSetButton;
    private LinearLayout mChannelLinearLayout;
    private Spinner mChannelSpinner;
    private TextView mChannelGetButton;
    private TextView mChannelSetButton;
    private Spinner mPowerSpinner;
    private TextView mPowerGetButton;
    private TextView mPowerSetButton;
    private Spinner mModulationSpinner;
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            switch (psData.Code) {
                case RcpMM.RCP_MM_UPDATE_FLASH:
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(false);
                        }
                    });
                    MessageUtil.Show("Update succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_ERASE_FLASH:
                    MessageUtil.Show("Erase succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_RESET:
                    MessageUtil.Show("Reset succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_SET_REGION:
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(true);
                        }
                    });
                    MessageUtil.Show("Set region succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_SET_CH:
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(true);
                        }
                    });
                    MessageUtil.Show("Set channel succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_SET_TX_PWR:
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(true);
                        }
                    });
                    MessageUtil.Show("Set power succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_SET_FH_LBT:
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(true);
                        }
                    });
                    MessageUtil.Show("Set freq and lbt settings succeed!", mBaseRes);
                    break;
                case RcpMM.RCP_MM_GET_TX_PWR: {
                    final int shortPower = psData.Payload[0];
                    MessageUtil.Show("Get power succeed!", mBaseRes);

                    mPowerSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mPowerSpinner.setSelection(shortPower, true);
                        }
                    });
                }
                break;
                case RcpMM.RCP_MM_GET_FH_LBT:
                    final boolean freq_hopp = ((psData.Payload[0] & 0xff) > 0 ? true : false);

                    MessageUtil.Show("Get freq and lbt settings succeed!", mBaseRes);

                    mFreqhoppingCheck.post(new Runnable() {
                        @Override
                        public void run() {
                            mFreqhoppingCheck.setChecked(freq_hopp);
                        }
                    });
                    break;
                case RcpMM.RCP_MM_GET_REGION:
                    final byte region = psData.Payload[0];
                    MessageUtil.Show("Get region succeed!", mBaseRes);
                    mRegionSpinner.post(new Runnable() {
                        @Override
                        public void run() {

                            mRegionSpinner.setSelection(region - 1, true);
                        }
                    });
                    break;
                case RcpMM.RCP_MM_GET_CH: {
                    final int index = psData.Payload[0];
                    MessageUtil.Show("Get channel succeed!", mBaseRes);
                    mChannelSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mChannelSpinner.setSelection(index, true);
                        }
                    });
                }
                break;
                case RcpMM.RCP_MM_GET_MODULATION: {
                    final int index = psData.Payload[0];
                    MessageUtil.Show("Get Modulation succeed!", mBaseRes);
                    mModulationSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mModulationSpinner.setSelection(index, true);
                        }
                    });
                }
                break;
                case RcpMM.RCP_MM_SET_MODULATION: {
                    mUpdateButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdateButton.setEnabled(true);
                        }
                    });
                    MessageUtil.Show("Set Modulation succeed!", mBaseRes);
                }
                break;
            }
        }
    };
    private TextView mModulationGetButton;
    private TextView mModulationSetButton;
    //-------------------base event-----------------
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.senior_region_value:
                    refreshChannel(position);
                    break;
                case R.id.senior_channel_value:

                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_button_reset:
                    Command(RcpMM.RCP_MM_RESET, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.base_button_erase:
                    askForErase();
                    break;
                case R.id.base_button_update:
                    askForUpdate();
                    break;

                case R.id.senior_modulation_get:
                    Command(RcpMM.RCP_MM_GET_MODULATION, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.senior_modulation_set:
                    byte mode = (byte) mModulationSpinner.getSelectedItemPosition();
                    Command(RcpMM.RCP_MM_SET_MODULATION, RcpBase.RCP_MSG_CMD, new byte[]{mode});
                    break;

                case R.id.senior_power_get:
                    Command(RcpMM.RCP_MM_GET_TX_PWR, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.senior_power_set:
                    byte powervalue = (byte) (mPowerSpinner.getSelectedItemPosition());
                    Command(RcpMM.RCP_MM_SET_TX_PWR, RcpBase.RCP_MSG_CMD, new byte[]{powervalue});
                    break;

                case R.id.senior_hopsettings_get:
                    Command(RcpMM.RCP_MM_GET_FH_LBT, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.senior_hopsettings_set:

                    byte freq_hopp = (byte) (mFreqhoppingCheck.isChecked() ? 1 : 0);
                    Command(RcpMM.RCP_MM_SET_FH_LBT, RcpBase.RCP_MSG_CMD, new byte[]{freq_hopp});
                    break;

                case R.id.senior_region_get:
                    Command(RcpMM.RCP_MM_GET_REGION, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.senior_region_set:
                    byte region = (byte) (mRegionSpinner.getSelectedItemPosition() + 1);
                    Command(RcpMM.RCP_MM_SET_REGION, RcpBase.RCP_MSG_CMD, new byte[]{region});
                    break;

                case R.id.senior_channel_get:
                    Command(RcpMM.RCP_MM_GET_CH, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.senior_channel_set:
                    byte cn = (byte) mChannelSpinner.getSelectedItemPosition();
                    Command(RcpMM.RCP_MM_SET_CH, RcpBase.RCP_MSG_CMD, new byte[]{cn});
                    break;
            }
        }
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

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    //-------------------base function------------
    private void initUI() {

        mResetButton = (TextView) findViewById(R.id.base_button_reset);
        mEraseButton = (TextView) findViewById(R.id.base_button_erase);
        mUpdateButton = (TextView) findViewById(R.id.base_button_update);

        mResetButton.setOnClickListener(onClickListener);
        mEraseButton.setOnClickListener(onClickListener);
        mUpdateButton.setOnClickListener(onClickListener);

        mFreqhoppingCheck = (CheckBox) findViewById(R.id.senior_freqhopping_value);
        mHoppsettingsGetButton = (TextView) findViewById(R.id.senior_hopsettings_get);
        mHoppsettingsSetButton = (TextView) findViewById(R.id.senior_hopsettings_set);

        mHoppsettingsGetButton.setOnClickListener(onClickListener);
        mHoppsettingsSetButton.setOnClickListener(onClickListener);

        mRegionSpinner = (Spinner) findViewById(R.id.senior_region_value);
        mRegionGetButton = (TextView) findViewById(R.id.senior_region_get);
        mRegionSetButton = (TextView) findViewById(R.id.senior_region_set);

        mRegionSpinner.setOnItemSelectedListener(onItemSelectedListener);

        mRegionGetButton.setOnClickListener(onClickListener);
        mRegionSetButton.setOnClickListener(onClickListener);

        mChannelLinearLayout = (LinearLayout) findViewById(R.id.senior_channel_layout);
        mChannelSpinner = (Spinner) findViewById(R.id.senior_channel_value);
        mChannelGetButton = (TextView) findViewById(R.id.senior_channel_get);
        mChannelSetButton = (TextView) findViewById(R.id.senior_channel_set);

        mChannelSpinner.setOnItemSelectedListener(onItemSelectedListener);

        mChannelGetButton.setOnClickListener(onClickListener);
        mChannelSetButton.setOnClickListener(onClickListener);

        mPowerSpinner = (Spinner) findViewById(R.id.senior_power_value);
        mPowerGetButton = (TextView) findViewById(R.id.senior_power_get);
        mPowerSetButton = (TextView) findViewById(R.id.senior_power_set);

        mPowerGetButton.setOnClickListener(onClickListener);
        mPowerSetButton.setOnClickListener(onClickListener);

        mModulationSpinner = (Spinner) findViewById(R.id.senior_modulation_value);
        mModulationGetButton = (TextView) findViewById(R.id.senior_modulation_get);
        mModulationSetButton = (TextView) findViewById(R.id.senior_modulation_set);

        mModulationGetButton.setOnClickListener(onClickListener);
        mModulationSetButton.setOnClickListener(onClickListener);

        initPower();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    Command(RcpMM.RCP_MM_GET_MODULATION, RcpBase.RCP_MSG_CMD);
                    Thread.sleep(40);
                    Command(RcpMM.RCP_MM_GET_TX_PWR, RcpBase.RCP_MSG_CMD);
                    Thread.sleep(40);
                    Command(RcpMM.RCP_MM_GET_FH_LBT, RcpBase.RCP_MSG_CMD);
                    Thread.sleep(40);
                    Command(RcpMM.RCP_MM_GET_REGION, RcpBase.RCP_MSG_CMD);
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
        setContentView(R.layout.activity_mm_senior);    //需要修改
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.title_senior);
        WakeLockUtil.KeepScreenOn(this);//高亮屏幕,禁止锁屏

        baseinfo_init();

        initUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //startActivity(new Intent().setClass(this, AdpmmActivity.class));
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
    //-------------------base event end-------------

    //-------------------define function-----------
    private void initPower() {

        List<String> mPowerList = new ArrayList<String>();
        for (int i = 0; i < 31; i++) {
            mPowerList.add(String.valueOf(i));
        }

        //实例化适配器,里面的参数可以参照UI大全那部分
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_display_style, R.id.txtvwSpinner, mPowerList);
        //这个是设置选项卡的显示格式的
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        //加载适配器
        mPowerSpinner.setAdapter(adapter);
    }

    private void refreshChannel(final int region) {
        mChannelSpinner.post(new Runnable() {
            @Override
            public void run() {

                List<String> mChannelList = new ArrayList<String>();
                double stepvalue = 0;
                switch (region) {
                    case 0:
                        for (int i = 0; i < 20; i++) {
                            stepvalue = 920.125f + 0.25f * i;
                            mChannelList.add(String.format("%.3f", stepvalue));
                        }
                        break;
                    case 1:
                        for (int i = 0; i < 20; i++) {
                            stepvalue = 840.125f + 0.25f * i;
                            mChannelList.add(String.format("%.3f", stepvalue));
                        }
                        break;
                    case 2:
                        for (int i = 0; i < 50; i++) {
                            stepvalue = 902.250f + 0.50f * i;
                            mChannelList.add(String.format("%.3f", stepvalue));
                        }
                        break;
                    case 3:
                        for (int i = 0; i < 15; i++) {
                            stepvalue = 865.100f + 0.2f * i;
                            mChannelList.add(String.format("%.3f", stepvalue));
                        }
                        break;
                    case 4:
                        for (int i = 0; i < 50; i++) {
                            stepvalue = 917.100f + 0.2f * i;
                            mChannelList.add(String.format("%.3f", stepvalue));
                        }
                        break;
                }

                //实例化适配器,里面的参数可以参照UI大全那部分
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_display_style, R.id.txtvwSpinner, mChannelList);
                //这个是设置选项卡的显示格式的
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                //加载适配器
                mChannelSpinner.setAdapter(adapter);
            }
        });
    }

    private void askForErase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.alert_diag_title)).
                setMessage(getString(R.string.are_you_sure_to_erase)).
                setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Command(RcpMM.RCP_MM_ERASE_FLASH, RcpBase.RCP_MSG_CMD, new byte[]{(byte) 0xFF});
                            }
                        }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }

    private void askForUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.alert_diag_title)).
                setMessage(getString(R.string.are_you_sure_to_update)).
                setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Command(RcpMM.RCP_MM_UPDATE_FLASH, RcpBase.RCP_MSG_CMD, new byte[]{(byte) 0x01});
                            }
                        }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }
    //-------------------define function end--------
}
