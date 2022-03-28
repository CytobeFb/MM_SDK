package com.ad.yeyoo.mm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.base.HexTextWatcher;
import com.ad.yeyoo.utils.ConverterUtil;
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

public class MMISO6CActivity extends Activity {


    public static final int LOCK_TYPE_OPEN = 0; // 开放
    public static final int LOCK_TYPE_PERMA_OPEN = 1; // 永久开放
    public static final int LOCK_TYPE_LOCK = 2; // 锁定
    public static final int LOCK_TYPE_PERMA_LOCK = 3; // 永久锁定
    public static final int LOCK_MEM_KILL = 1; // 销毁密码
    public static final int LOCK_MEM_ACCESS = 2; // 访问密码
    public static final int LOCK_MEM_EPC = 3; // EPC
    public static final int LOCK_MEM_TID = 4; // TID
    public static final int LOCK_MEM_USER = 5; // USER
    //-------------------base info end------------

    //-------------------define info--------------
    int selectstatus = 0;
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
    private TextView mIdentifyButton;
    private TextView mSelectedButton;
    private TextView mUnSelectedButton;
    private TextView mReadButton;
    private TextView mWriteButton;
    private Spinner mBlockSpinner;
    private EditText mAddrEdit;
    private EditText mLengthEdit;
    private EditText mAccessEditHex;
    private EditText mEpcEdit;
    private EditText mReadEdit;
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            final ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            switch (psData.Code) {
                case RcpMM.RCP_MM_READ_C_UII:
                    if (psData.Length > 0 && (psData.Type == 0x05 || psData.Type == 0x02)) {
                        int pcepclen = GetCodelen(psData.Payload[1]);
                        int datalen = psData.Length - 2;//去掉天线号去掉rssi

                        String tag_pc = SioBase.ByteArrayToHexString(psData.Payload, 1, 2);
                        final String tag_epc = SioBase.ByteArrayToHexString(psData.Payload, 3, pcepclen - 2);
                        String tag_rssi = CalcTagRssi(psData.Payload[psData.Length - 1]);

                        mEpcEdit.post(new Runnable() {
                            @Override
                            public void run() {
                                mEpcEdit.setText(tag_epc);
                            }
                        });
                        MessageUtil.Show("New Tag Read Succeed!", mBaseRes);
                    } else if (psData.Type == 0 || psData.Type == 1) {
                    }
                    break;
                case RcpMM.RCP_MM_READ_C_DT:
                    if (psData.Type == 0x00) {
                        if (psData.Length > 0) {
                            int pcepclen = GetCodelen(psData.Payload[1]);
                            int datalen = psData.Length - 1;//去掉天线号

                            String tag_pc = SioBase.ByteArrayToHexString(psData.Payload, 1, 2);
                            final String tag_epc = SioBase.ByteArrayToHexString(psData.Payload, 3, pcepclen - 2);
                            final String tag_dat = SioBase.ByteArrayToHexString(psData.Payload, 1 + pcepclen, datalen - pcepclen);

                            mReadEdit.post(new Runnable() {
                                @Override
                                public void run() {
                                    mReadEdit.setText(tag_dat);
                                }
                            });
                            //MessageUtil.List("epc :"+tag_epc ,mBaseRes);
                            //MessageUtil.List("dat :"+tag_dat ,mBaseRes);
                        }
                    } else if (psData.Type == 0x01) {
                        MessageUtil.List("READ ERROR", mBaseRes);
                    }
                    break;
                case RcpMM.RCP_MM_WRITE_C_DT:
                    if (psData.Type == 0x00) {
                        int pcepclen = GetCodelen(psData.Payload[1]);
                        int datalen = psData.Length - 1;//去掉天线号
                        String tag_pc = SioBase.ByteArrayToHexString(psData.Payload, 1, 2);
                        final String tag_epc = SioBase.ByteArrayToHexString(psData.Payload, 3, pcepclen - 2);
                        MessageUtil.List("WRITE succeed", mBaseRes);

                    } else if (psData.Type == 0x01) {
                        MessageUtil.List("WRITE ERROR", mBaseRes);
                    }
                    break;
                case RcpMM.RCP_MM_LOCK_C:
                    if (psData.Type == 0x00) {
                        MessageUtil.List("Lock Action succeed", mBaseRes);

                    } else if (psData.Type == 0x01) {
                        MessageUtil.List("Lock Action ERROR", mBaseRes);
                    }
                    break;
                case RcpMM.RCP_MM_KILL_RECOM_C:
                    if (psData.Type == 0x00) {
                        MessageUtil.List("KILL Action succeed", mBaseRes);

                    } else if (psData.Type == 0x01) {
                        MessageUtil.List("KILL Action ERROR", mBaseRes);
                    }
                    break;
                case RcpMM.RCP_MM_SET_ACCESS_EPC_MATCH:
                    if (psData.Type == 0x00) {
                        MessageUtil.List("SELECT Action succeed", mBaseRes);
                        if (selectstatus == 0) {

                            mEpcEdit.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEpcEdit.setEnabled(false);
                                }
                            });
                        } else {
                            mReadEdit.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEpcEdit.setEnabled(true);
                                }
                            });
                        }
                    } else if (psData.Type == 0x01) {
                        MessageUtil.List("SELECT Action ERROR", mBaseRes);
                    }
                    break;
            }
        }
    };
    private EditText mWriteEdit;
    private Spinner mLockmemSpinner;
    private Spinner mLocktypeSpinner;
    private TextView mLockButton;
    private EditText mKillEditHex;
    private TextView mKillButton;
    //-------------------base event-----------------
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageUtil.Show("", mBaseRes);

            byte[] btAccess = new byte[]{0, 0, 0, 0};
            byte btMemBank = 0x00;
            byte btStartAddress = 0;
            byte btWordLength = 0;

            btAccess = ConverterUtil.HexStringToBytes(mAccessEditHex.getText().toString().toUpperCase());

            if (btAccess.length != 4 && v.getId() != R.id.base_button_identify) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.param_password_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            btMemBank = (byte) (mBlockSpinner.getSelectedItemPosition());
            btStartAddress = Byte.parseByte(mAddrEdit.getText().toString());
            btWordLength = Byte.parseByte(mLengthEdit.getText().toString());

            switch (v.getId()) {
                case R.id.iso6c_epc_identify:
                    Command(RcpMM.RCP_MM_READ_C_UII, RcpBase.RCP_MSG_CMD);
                    break;
                case R.id.iso6c_epc_selected:
                    String selecttagstring = mEpcEdit.getText().toString().toUpperCase();
                    if (selecttagstring.length() > 0 && selecttagstring.length() % 2 == 0)
                        selectTag(mEpcEdit.getText().toString().toUpperCase());
                    break;
                case R.id.iso6c_epc_unselected:
                    unselectTag();
                    break;
                case R.id.iso6c_epc_read:
                    read(btAccess, btMemBank, btStartAddress, btWordLength);
                    break;
                case R.id.iso6c_epc_write:
                    byte[] btAryData = null;
                    try {
                        btAryData = ConverterUtil.HexStringToBytes(mWriteEdit.getText().toString().toUpperCase());
                        btWordLength = (byte) (btAryData.length & 0xFF);
                        btWordLength /= 2;
                    } catch (Exception e) {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.param_data_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (btAryData == null || btAryData.length <= 0) {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.param_data_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mLengthEdit.setText(String.valueOf(btWordLength & 0xFF));
                    write(btAccess, btMemBank, btStartAddress, btWordLength, btAryData);
                    break;
                case R.id.iso6c_lock_button:
                    int locktype = mLockmemSpinner.getSelectedItemPosition();
                    int lockaction = mLocktypeSpinner.getSelectedItemPosition();

                    lock(btAccess, GetLockData(locktype, lockaction));
                    break;
                case R.id.iso6c_kill_button:
                    byte[] btkill = new byte[]{0, 0, 0, 0};
                    btkill = ConverterUtil.HexStringToBytes(mKillEditHex.getText().toString().toUpperCase());

                    if (btkill.length != 4) {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.param_password_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    kill(btkill);
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

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    //-------------------base function------------
    private void initUI() {

        mIdentifyButton = (TextView) findViewById(R.id.iso6c_epc_identify);

        mSelectedButton = (TextView) findViewById(R.id.iso6c_epc_selected);
        mUnSelectedButton = (TextView) findViewById(R.id.iso6c_epc_unselected);

        mReadButton = (TextView) findViewById(R.id.iso6c_epc_read);
        mWriteButton = (TextView) findViewById(R.id.iso6c_epc_write);

        mIdentifyButton.setOnClickListener(onClickListener);

        mSelectedButton.setOnClickListener(onClickListener);
        mUnSelectedButton.setOnClickListener(onClickListener);

        mReadButton.setOnClickListener(onClickListener);
        mWriteButton.setOnClickListener(onClickListener);

        mAccessEditHex = (EditText) findViewById(R.id.iso6c_access_value);
        mBlockSpinner = (Spinner) findViewById(R.id.iso6c_block_value);
        mAddrEdit = (EditText) findViewById(R.id.iso6c_addr_value);
        mLengthEdit = (EditText) findViewById(R.id.iso6c_len_value);

        mEpcEdit = (EditText) findViewById(R.id.iso6c_epc_value);
        mReadEdit = (EditText) findViewById(R.id.iso6c_read_value);
        mWriteEdit = (EditText) findViewById(R.id.iso6c_write_value);
        mReadEdit.addTextChangedListener(new HexTextWatcher(mReadEdit));
        mReadEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mReadEdit.setGravity(Gravity.TOP);
        mReadEdit.setSingleLine(false);
        mReadEdit.setHorizontallyScrolling(false);
        mWriteEdit.addTextChangedListener(new HexTextWatcher(mWriteEdit));
        mWriteEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mWriteEdit.setGravity(Gravity.TOP);
        mWriteEdit.setSingleLine(false);
        mWriteEdit.setHorizontallyScrolling(false);

        mLockmemSpinner = (Spinner) findViewById(R.id.iso6c_lockmem_value);
        mLocktypeSpinner = (Spinner) findViewById(R.id.iso6c_locktype_value);
        mKillEditHex = (EditText) findViewById(R.id.iso6c_kill_value);

        mLockButton = (TextView) findViewById(R.id.iso6c_lock_button);
        mKillButton = (TextView) findViewById(R.id.iso6c_kill_button);
        mLockButton.setOnClickListener(onClickListener);
        mKillButton.setOnClickListener(onClickListener);

        mBlockSpinner.setSelection(1, true);
        mAddrEdit.setText(String.valueOf(2));
        mLengthEdit.setText(String.valueOf(6));

        mLockmemSpinner.setSelection(2, true);
        mLocktypeSpinner.setSelection(2, true);
    }

    //-------------------base function end----------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mm_iso6c);    //需要修改

        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);

        this.setTitle(R.string.title_6c);
        WakeLockUtil.KeepScreenOn(this);//禁止锁屏

        baseinfo_init();

        initUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //startActivity(new Intent().setClass(this, PhychipsActivity.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    //-------------------base event end-------------

    //-------------------define function------------

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private String CalcTagRssi(byte Data) {

        int rssidBm = Data; // rssidBm is negative && in bytes
        rssidBm += 20;
        rssidBm -= 3;
        return String.valueOf(rssidBm);

    }

    private int GetCodelen(byte iData) {
        return (((iData >> 3) + 1) * 2);
    }

    //设置选中EPC
    private void selectTag(String hexstring) {
        byte[] epc = SioBase.HexStringToBytes(hexstring);
        byte epcLen = (byte) (epc.length > 12 ? 12 : epc.length);
        byte[] selectdata = new byte[epcLen + 2];
        selectdata[0] = 0x02;
        selectdata[1] = epcLen;
        for (int i = 0; i < epcLen; i++)
            selectdata[i + 2] = epc[i];
        Command(RcpMM.RCP_MM_SET_ACCESS_EPC_MATCH, RcpBase.RCP_MSG_CMD, selectdata);
        selectstatus = 0;
    }

    //取消选中EPC
    private void unselectTag() {
        Command(RcpMM.RCP_MM_SET_ACCESS_EPC_MATCH, RcpBase.RCP_MSG_CMD, new byte[]{0x00});
        selectstatus = 1;
    }

    private void read(byte[] ap, byte mb, byte sa, byte dl) {
        byte[] snddata;
        snddata = new byte[7];
        for (int i = 0; i < 4; i++)
            snddata[i] = ap[i];
        snddata[4] = mb;
        snddata[5] = sa;
        snddata[6] = dl;
        Command(RcpMM.RCP_MM_READ_C_DT, RcpBase.RCP_MSG_CMD, snddata);
    }

    private void write(byte[] ap, byte mb, byte sa, byte dl, byte[] dt) {
        byte[] snddata;
        snddata = new byte[7 + dl * 2];
        for (int i = 0; i < 4; i++)
            snddata[i] = ap[i];
        snddata[4] = mb;
        snddata[5] = sa;
        snddata[6] = dl;
        for (int i = 0; i < dl * 2; i++)
            snddata[i + 7] = dt[i];
        Command(RcpMM.RCP_MM_WRITE_C_DT, RcpBase.RCP_MSG_CMD, snddata);
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="type">0~4</param>
    /// <param name="action">0~3</param>
    /// <returns></returns>
    private byte[] GetLockData(int type, int action) {
        byte[] lockdata;

        int intlockkey = 0;
        int lockindex = (type) * 2;
        //加载ACTION
        intlockkey = (action << lockindex);

        //加载mask
        int lockmask = 0;
        switch (action) {
            case 0:
                lockmask = 2;
                break;
            case 1:
                lockmask = 1;
                break;
            case 2:
                lockmask = 2;
                break;
            case 3:
                lockmask = 3;
                break;
        }
        intlockkey += (lockmask << (lockindex + 10));


        lockdata = new byte[3];
        lockdata[0] = (byte) (intlockkey >> 16);
        lockdata[1] = (byte) (intlockkey >> 8);
        lockdata[2] = (byte) (intlockkey);
        return lockdata;
    }

    private void lock(byte[] ap, byte[] lockdat) {
        byte[] snddata;
        snddata = new byte[7];
        for (int i = 0; i < 4; i++)
            snddata[i] = ap[i];
        for (int i = 0; i < 3; i++)
            snddata[i + 4] = lockdat[i];
        Command(RcpMM.RCP_MM_LOCK_C, RcpBase.RCP_MSG_CMD, snddata);
    }

    private void kill(byte[] kp) {
        byte[] snddata;
        snddata = new byte[5];
        for (int i = 0; i < 4; i++)
            snddata[i] = kp[i];
        snddata[4] = 0;
        Command(RcpMM.RCP_MM_KILL_RECOM_C, RcpBase.RCP_MSG_CMD, snddata);
    }
    //-------------------define function end--------
}
