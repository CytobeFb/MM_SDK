package com.ad.yeyoo.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolEventArg;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.ad.rcp.TagID;
import com.ad.sio.OnCommListener;
import com.ad.sio.SioBase;
import com.ad.sio.StatusEventArg;
import com.ad.yeyoo.PubDemoParaActivity;
import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.bean.CheckBean;
import com.ad.yeyoo.bean.RelationListBean;
import com.ad.yeyoo.utils.ConverterUtil;
import com.ad.yeyoo.utils.MessageUtil;
import com.ad.yeyoo.utils.PreferenceUtil;
import com.ad.yeyoo.utils.SoundPoolUtil;
import com.ad.yeyoo.utils.UrlUtils;
import com.ad.yeyoo.utils.WakeLockUtil;
import com.chice.scangun.ScanGun;
import com.google.gson.Gson;
import com.lingber.mycontrol.datagridview.DataGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by endyc on 2018-06-20.
 */

public class BusinessRelationActivity extends Activity {

    //-------------------TAG ID show short id--------
    //
    private static final int REQUEST_TAG_TYPE_SECURE = 0;
    //-------------------base info----------------
    private Context mContext;
    private TextView mBaseRes;
    private SioBase mSioBase;
    private RcpBase mRcpBase;
    private ScanGun mScanGun = null;
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
    private int mReadCount = 0;
    private int mReadMaxCount = 0;
    private int mReadStopCount = 0;
    //-------------------base info end------------
    private boolean mReadAuto = false;
    private int mWorkType = 0;
    private boolean mSingleRead = false;
    private int ReadRtnDelay = 0;
    //????????????
    private TextView mBaseCommand;
    private TextView mBaseClear;
    private TextView mMaxtagTextView;
    private TextView mStoptagTextView;
    private ListView mDataListView;
    private ListView lv_data;
    private DataGridView mDataGridView;
    private TextView tv_scan;
    private EditText ed_order;
    private TextView tv_titel;
    private TextView tv_ensure,tv_cancel;
    private ArrayList<TagID> mTagIDArrayList = new ArrayList<TagID>();
    private ArrayList<Map<String, Object>> mDataListMap;
    private List<RelationListBean.DataBean> relationList=new ArrayList<>();
    private RecvRunnable hmR = null;
    private String orderId="";
    //-------------------define function------------
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_button_identify:
                    if (mWorkType == 0) {
                        mReadAuto = !mReadAuto;
                        changeAutoRead();
                        mSingleRead = false;
                        if (mReadAuto) {
                            ClearList();
                            try {
                                mReadStopCount = Integer.parseInt(mStoptagTextView.getText().toString());
                            } catch (Exception e) {
                                mReadStopCount = 1;
                            }
                            hmR = new RecvRunnable(mReadStopCount);
                            Thread t = new Thread(hmR);
                            t.start();
                        } else {
                            if (hmR != null) hmR.stop();
                        }
                    } else if (mWorkType == 1) {
                        mReadAuto = !mReadAuto;

                        Command(RcpMM.RCP_MM_CTRL_AUTO_READ, RcpBase.RCP_MSG_CMD, new byte[]{(byte) (mReadAuto ? 1 : 0)});
                        changeAutoRead();
                    }
                    break;
                case R.id.base_button_clear:
                    ClearList();
                    break;
                case R.id.tv_scan:
                    if (mWorkType == 0) {
                        mReadAuto = !mReadAuto;
                        changeAutoRead2();
                        mSingleRead = false;
                        if (mReadAuto) {
                            ClearList();
                            try {
                                mReadStopCount = Integer.parseInt(mStoptagTextView.getText().toString());
                            } catch (Exception e) {
                                mReadStopCount = 1;
                            }
                            hmR = new RecvRunnable(mReadStopCount);
                            Thread t = new Thread(hmR);
                            t.start();
                        } else {
                            if (hmR != null) hmR.stop();
                        }
                    } else if (mWorkType == 1) {
                        mReadAuto = !mReadAuto;

                        Command(RcpMM.RCP_MM_CTRL_AUTO_READ, RcpBase.RCP_MSG_CMD, new byte[]{(byte) (mReadAuto ? 1 : 0)});
                        changeAutoRead2();
                    }
                    break;
                case R.id.tv_ensure:
                    if (!mTagIDArrayList.isEmpty()) {
                        String ids="";
                        for (int i=0;i<mTagIDArrayList.size();i++){
                            ids+=getShortTag(mTagIDArrayList.get(i).getEpc())+",";
                        }
                        ids=ids.substring(0,ids.length()-1);
                        confirmOrderForGoods(ids,orderId);
                    }
//                    confirmOrderForGoods("0000090000000002EBFB06BC,000008000000000000000000","d7c8e7f9-cc9a-4ec1-b2b9-1cb17581266e");
                    break;
                case R.id.tv_cancel:
                    if (!mTagIDArrayList.isEmpty()) {
                        String ids="";
                        for (int i=0;i<mTagIDArrayList.size();i++){
                            ids+=getShortTag(mTagIDArrayList.get(i).getEpc())+",";
                        }
                        ids=ids.substring(0,ids.length()-1);
                        cancelOrderForGoods(ids);
                    }
//                    cancelOrderForGoods("000008000000000000000000");
                    break;
            }
        }

        ;
    };
    private TextView mBaseSettings;
    //-------------------define info end----------
    private Integer mUsed = 0;
    private Integer mType = 1;
    private Integer mStart = 0;
    //-------------------base function end----------
    private Integer mByte = 12;
    private OnProtocolListener onProtocolListener = new OnProtocolListener() {

        @Override
        public void onReceived(Object object, ProtocolEventArg protocolEventArg) {
            final ProtocolPacket psData = protocolEventArg.getProtocolPacket();

            switch (psData.Code) {
                case RcpMM.RCP_MM_READ_C_UII:
                    if (psData.Length > 0 && (psData.Type == 0x05 || psData.Type == 0x02)) {
                        int pcepclen = GetCodelen(psData.Payload[1]);
                        int datalen = psData.Length - 2;//?????????????????????rssi

                        String tag_pc = SioBase.ByteArrayToHexString(psData.Payload, 1, 2);
                        String tag_epc = "";
                        String tag_rssi = CalcTagRssi(psData.Payload[psData.Length - 1]);

                        if ((datalen - pcepclen) > 0) {
                            tag_epc = SioBase.ByteArrayToHexString(psData.Payload, 3, datalen - 2);
                        } else {
                            tag_epc = SioBase.ByteArrayToHexString(psData.Payload, 3, pcepclen - 2);
                        }
                        addToList(tag_pc, tag_epc, tag_rssi);
                        MessageUtil.Show("New Tag Read Succeed! [" + (++mReadCount) + "]", mBaseRes);
                    } else if (psData.Type == 0 || psData.Type == 1) {
                        if (hmR != null) hmR.setCommand();
                    }
                    break;
                case RcpMM.RCP_MM_PARA:
                    if (psData.Length > 0 && psData.Type == 0) {

                        final int commmode = psData.Payload[0];
                        final int workmode = psData.Payload[1];

                        changeWorkMode(workmode, commmode);

                        MessageUtil.Show("CommMode: " + String.valueOf(commmode) + " WorkMode: " + String.valueOf(workmode), mBaseRes);
                    }
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
    //-------------------base event end-------------

    private void Command(byte nCode, byte nType) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType));
    }

    private void Command(byte nCode, byte nType, byte[] ArgByte) {
        mSioBase.send(RcpBase.buildCmdPacketByte(nCode, nType, ArgByte));
    }

    //-------------------base function------------
    private void initUI() {
        tv_titel=findViewById(R.id.tv_titel);
        lv_data=findViewById(R.id.lv_data);
        mDataGridView = findViewById(R.id.datagridview);
        tv_scan=findViewById(R.id.tv_scan);
        ed_order=findViewById(R.id.ed_order);
        tv_ensure=findViewById(R.id.tv_ensure);
        tv_cancel=findViewById(R.id.tv_cancel);
        mStoptagTextView = (EditText) findViewById(R.id.listitem_counttag_value);
        mMaxtagTextView = (TextView) findViewById(R.id.listitem_maxtag_value);
        mBaseClear = (TextView) findViewById(R.id.base_button_clear);
        mBaseClear.setOnClickListener(onClickListener);
        tv_scan.setOnClickListener(onClickListener);
        tv_titel.setOnClickListener(onClickListener);
        tv_ensure.setOnClickListener(onClickListener);
        tv_cancel.setOnClickListener(onClickListener);

        ed_order.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // ???????????????????????????????????????
//                    ed_order.setText("");
                } else {
                    // ???????????????????????????????????????
                }
            }
        });

        ed_order.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    //???????????????
                    orderId=ed_order.getText().toString();
                    ed_order.setText("");
                }
                return false;
            }
        });

        /*mScanGun = new ScanGun(new ScanGun.ScanGunCallBack() {

            @Override
            public void onScanFinish(String scanResult) {
                if (!TextUtils.isEmpty(scanResult)) {
                    orderId=scanResult;
                    ed_order.setText(scanResult);
                }
            }
        });*/

        mBaseCommand = (TextView) findViewById(R.id.base_button_identify);

        mBaseCommand.setOnClickListener(onClickListener);

        mDataListView = (ListView) findViewById(R.id.demo_listvalue);

        // ????????????
        mDataGridView.setColunms(6);
        // ??????????????????
        mDataGridView.setHeaderContentByStringId(new int[]{R.string.colom1, R.string.colom2, R.string.colom3
                , R.string.colom4, R.string.colom5, R.string.colom6});
        // ????????????
        mDataGridView.setFieldNames(new String[]{"rowNumber","tagName","kuanhao","productName","color","guige"});
        // ??????column??????
        mDataGridView.setColunmWeight(new float[]{1,2,2,3,2,2});
        // ???????????????????????????
        mDataGridView.setCellContentView(new Class[]{TextView.class, TextView.class, TextView.class, TextView.class, TextView.class, TextView.class});
        // ???????????????
        mDataGridView.setDataSource(relationList);
        // ??????????????????
        mDataGridView.setSelectedMode(1);
        mDataGridView.setHeaderHeight(48);
        // ????????????
//        mDataGridView.setFlipOverEnable(true, 9, getFragmentManager());
        // ???????????????
        mDataGridView.initDataGridView();

/*
        mDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView epcTextview = (TextView) view.findViewById(R.id.listitem_card_item_tag);
                String epc = epcTextview.getText().toString();
                Integer intEpcLen =epc.length()>24?24:epc.length();
                Integer intOtherLen =epc.length()>intEpcLen?(epc.length()-intEpcLen):0;
                String mMsg = "";
                mMsg += "EPC:" + epc.substring(0, intEpcLen);
                if(intOtherLen>0)
                mMsg += "\nOTHER:" + epc.substring(intEpcLen, intOtherLen);
                refreshResView(mMsg, 3);
            }
        });
*/
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    Command(RcpMM.RCP_MM_PARA, RcpBase.RCP_MSG_GET);
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 100);*/
        changeAutoRead2();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        mBaseRes = (TextView) findViewById(R.id.base_res_value);


        this.setTitle(R.string.title_demo);
        WakeLockUtil.KeepScreenOn(this);//????????????
        SoundPoolUtil.initSoundPool(mContext);

        baseinfo_init();

        initUI();

        auto();

        initShortTagUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else if (keyCode == 136) {
            if (mReadAuto == false) {
                mSingleRead = true;
                Command(RcpMM.RCP_MM_READ_C_UII, RcpBase.RCP_MSG_CMD);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (hmR != null) hmR.stop();
        super.onDestroy();
    }

    public void auto(){
        if (mWorkType == 0) {
            mReadAuto = !mReadAuto;
            changeAutoRead2();
            mSingleRead = false;
            if (mReadAuto) {
                ClearList();
                try {
                    mReadStopCount = Integer.parseInt(mStoptagTextView.getText().toString());
                } catch (Exception e) {
                    mReadStopCount = 1;
                }
                hmR = new RecvRunnable(mReadStopCount);
                Thread t = new Thread(hmR);
                t.start();
            } else {
                if (hmR != null) hmR.stop();
            }
        } else if (mWorkType == 1) {
            mReadAuto = !mReadAuto;

            Command(RcpMM.RCP_MM_CTRL_AUTO_READ, RcpBase.RCP_MSG_CMD, new byte[]{(byte) (mReadAuto ? 1 : 0)});
            changeAutoRead2();
        }
    }

    private void changeWorkMode(final int workmode, final int commmode) {
        mBaseCommand.post(new Runnable() {
            @Override
            public void run() {
                mWorkType = workmode;
                if (mWorkType == 0) {
                    mReadAuto = false;
                    changeAutoRead2();
                } else if (mWorkType == 1) {
                    mReadAuto = true;
                    changeAutoRead2();
                }
            }
        });
    }
    //-------------------define function end--------

    private void changeAutoRead() {
        mBaseCommand.post(new Runnable() {
            @Override
            public void run() {
                mBaseCommand.setText(mReadAuto ? "Stop Auto" : "Start Auto");
            }
        });
    }

    private void changeAutoRead2() {
        tv_scan.post(new Runnable() {
            @Override
            public void run() {
                tv_scan.setText(mReadAuto ? "????????????" : "????????????");
            }
        });
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

    //????????????EPC?????????LISTVIEW
    private void addToList(final String tag_pc, final String tag_epc, final String tag_rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SoundPoolUtil.play(0);
                //?????????????????????
                if (mTagIDArrayList.isEmpty()) {
                    mTagIDArrayList.add(new TagID(tag_pc, tag_epc, tag_rssi));
                    mReadMaxCount = mTagIDArrayList.size();
                    mMaxtagTextView.setText(String.valueOf(mReadMaxCount));
                    if (hmR != null) hmR.setStoptagsCount(mReadMaxCount);
                } else {
                    for (int i = 0; i < mTagIDArrayList.size(); i++) {
                        TagID mEPC = mTagIDArrayList.get(i);
                        //list?????????EPC
                        if (tag_epc.equals(mEPC.getEpc())) {
                            mEPC.setRssi(tag_rssi);
                            mEPC.setCount(mEPC.getCount() + 1);
                            mTagIDArrayList.set(i, mEPC);
                            break;
                        } else if (i == (mTagIDArrayList.size() - 1)) {
                            //list????????????epc
                            if (!mSingleRead && mReadStopCount == mReadMaxCount) break;
                            mTagIDArrayList.add(new TagID(tag_pc, tag_epc, tag_rssi));
                            mReadMaxCount = mTagIDArrayList.size();
                            mMaxtagTextView.setText(String.valueOf(mReadMaxCount));
                            if (hmR != null) {
                                if (hmR.setStoptagsCount(mReadMaxCount)) {
                                    mReadAuto = false;
                                    changeAutoRead2();
                                }
                            }
                            break;
                        }
                    }
                }
                //??????????????????ListView
                mDataListMap = new ArrayList<Map<String, Object>>();
                int idcount = 1;
                for (TagID epcdata : mTagIDArrayList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("ID", idcount++);
                    map.put("TAG PC", epcdata.getPc());
                    map.put("TAG ID", getShortTag(epcdata.getEpc()));
                    map.put("RSSI", epcdata.getRssi());
                    map.put("COUNT", epcdata.getCount());
                    mDataListMap.add(map);
                }
                lv_data.setAdapter(new SimpleAdapter(mContext,
                        mDataListMap, R.layout.listitem_pcepc_item,
                        new String[]{"ID", "TAG PC", "TAG ID", "RSSI", "COUNT"},
                        new int[]{R.id.listitem_card_item_no, R.id.listitem_card_item_pc, R.id.listitem_card_item_tag, R.id.listitem_card_item_rssi, R.id.listitem_card_item_count}));
            }
        });
    }

    //????????????EPC?????????LISTVIEW
    private void addToList2(final String tag_pc, final String tag_epc, final String tag_rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SoundPoolUtil.play(0);
                //?????????????????????
                if (mTagIDArrayList.isEmpty()) {
                    mTagIDArrayList.add(new TagID(tag_pc, tag_epc, tag_rssi));
                    mReadMaxCount = mTagIDArrayList.size();
                    mMaxtagTextView.setText(String.valueOf(mReadMaxCount));
                    if (hmR != null) hmR.setStoptagsCount(mReadMaxCount);
                } else {
                    for (int i = 0; i < mTagIDArrayList.size(); i++) {
                        TagID mEPC = mTagIDArrayList.get(i);
                        //list?????????EPC
                        if (tag_epc.equals(mEPC.getEpc())) {
                            mEPC.setRssi(tag_rssi);
                            mEPC.setCount(mEPC.getCount() + 1);
                            mTagIDArrayList.set(i, mEPC);
                            break;
                        } else if (i == (mTagIDArrayList.size() - 1)) {
                            //list????????????epc
                            if (!mSingleRead && mReadStopCount == mReadMaxCount) break;
                            mTagIDArrayList.add(new TagID(tag_pc, tag_epc, tag_rssi));
                            mReadMaxCount = mTagIDArrayList.size();
                            mMaxtagTextView.setText(String.valueOf(mReadMaxCount));
                            if (hmR != null) {
                                if (hmR.setStoptagsCount(mReadMaxCount)) {
                                    mReadAuto = false;
                                    changeAutoRead();
                                }
                            }
                            break;
                        }
                    }
                }
                //??????????????????ListView
                mDataListMap = new ArrayList<Map<String, Object>>();
                int idcount = 1;
                for (TagID epcdata : mTagIDArrayList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("ID", idcount++);
                    map.put("TAG PC", epcdata.getPc());
                    map.put("TAG ID", getShortTag(epcdata.getEpc()));
                    map.put("RSSI", epcdata.getRssi());
                    map.put("COUNT", epcdata.getCount());
                    mDataListMap.add(map);
                }
                mDataListView.setAdapter(new SimpleAdapter(mContext,
                        mDataListMap, R.layout.listitem_pcepc_item,
                        new String[]{"ID", "TAG PC", "TAG ID", "RSSI", "COUNT"},
                        new int[]{R.id.listitem_card_item_no, R.id.listitem_card_item_pc, R.id.listitem_card_item_tag, R.id.listitem_card_item_rssi, R.id.listitem_card_item_count}));
            }
        });
    }

    private void ClearList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    mTagIDArrayList.clear();
                    mDataListMap.clear();
                    mDataListView.setAdapter(new SimpleAdapter(mContext,
                            mDataListMap, R.layout.listitem_pcepc_item,
                            new String[]{"ID", "TAG PC", "TAG ID", "RSSI", "COUNT"},
                            new int[]{R.id.listitem_card_item_no, R.id.listitem_card_item_pc,
                                    R.id.listitem_card_item_tag, R.id.listitem_card_item_rssi, R.id.listitem_card_item_count}));
                    mMaxtagTextView.setText("0");
                    mReadCount = 0;

                } catch (Exception e) {
                    return;
                }
            }
        });
    }

    private void initShortTagUI() {
        mBaseSettings = (TextView) findViewById(R.id.base_demo_para_settings);
        mBaseSettings.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View v) {
                                                 switch (v.getId()) {
                                                     case R.id.base_demo_para_settings:
                                                         Intent serverIntent = new Intent().setClass(mContext, PubDemoParaActivity.class);
                                                         startActivityForResult(serverIntent, REQUEST_TAG_TYPE_SECURE);
                                                         break;
                                                 }
                                             }
                                         }
        );

        initShortTagType();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAG_TYPE_SECURE:
                // When BtDeviceScanActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    initShortTagType();
                }
                break;
        }
    }

    private void initShortTagType() {
        String strUsed = PreferenceUtil.getSettingNote(mContext, PubDemoParaActivity.EXTRAS_TAG_SAVENAME, PubDemoParaActivity.EXTRAS_TAG_OUTUSED);
        String strType = PreferenceUtil.getSettingNote(mContext, PubDemoParaActivity.EXTRAS_TAG_SAVENAME, PubDemoParaActivity.EXTRAS_TAG_OUTTYPE);
        String strStart = PreferenceUtil.getSettingNote(mContext, PubDemoParaActivity.EXTRAS_TAG_SAVENAME, PubDemoParaActivity.EXTRAS_TAG_OUTSTART);
        String strByte = PreferenceUtil.getSettingNote(mContext, PubDemoParaActivity.EXTRAS_TAG_SAVENAME, PubDemoParaActivity.EXTRAS_TAG_OUTBYTE);

        if (strUsed == null) mUsed = 0;
        else mUsed = Integer.parseInt(strUsed);
        if (strType == null) mType = 1;
        else mType = Integer.parseInt(strType);
        if (strStart == null) mStart = 0;
        else mStart = Integer.parseInt(strStart);
        if (strByte == null) mByte = 12;
        else mByte = Integer.parseInt(strByte);


        String strTagValue = "TYPE: ";
        switch (mType) {
            case 0:
                strTagValue += "Decimal";
                break;
            case 1:
                strTagValue += "Hex";
                break;
            case 2:
                strTagValue += "Wiegand";
                break;
            default:
                strTagValue += "Error";
                break;
        }

        strTagValue += " START: " + mStart;
        strTagValue += " BYTES: " + mByte;
        //if(mUsed>0)
        //    mBaseSettingsValue.setText(strTagValue);
        //else
        //    mBaseSettingsValue.setText("Not Used Short Id.");
    }

    private String getShortTag(String epc) {
        if (mUsed > 0) {
            return ConverterUtil.GetTagValueForHexString(epc, mType, mStart, mByte);
        } else {
            return epc;
        }
    }

    private class RecvRunnable implements Runnable {
        public boolean command = true;
        public boolean running = true;
        public int Stoptags = 20;
        public int StoptagsCount = 0;

        public RecvRunnable(int tags) {
            Stoptags = tags;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int commanddelay = 0;
            while (running) {
                try {
                    if (command) {
                        command = false;
                        commanddelay = 0;
                        Command(RcpMM.RCP_MM_READ_C_UII, RcpBase.RCP_MSG_CMD);
                    } else {
                        if (commanddelay++ > 500) {
                            commanddelay = 0;
                            command = true;
                        }
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    if (running) {
                        running = false;
                        break;
                    }
                }
            }
        }

        /**
         * Stop/abort listening for data events.
         */
        public void stop() {
            running = false;
            //usbPipe.abortAllSubmissions();
        }

        public void setCommand() {
            command = true;
            //usbPipe.abortAllSubmissions();
        }

        public boolean setStoptagsCount(int tags) {
            if (tags == Stoptags) {
                running = false;
                return true;
            }
            return false;
            //usbPipe.abortAllSubmissions();
        }
    }
    //-------------------TAG ID show short id--------


    public void getLabelForGoods(String ids){
        relationList=new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        HttpParams params=new HttpParams();
        params.put("ids",ids);
        if (BaseApplication.isNetworkAvailable(BusinessRelationActivity.this)) {
            OkGo.<String>post(UrlUtils.LABEL_FOR_GOODS)     // ?????????????????????url
                    .tag(this)                       // ????????? tag, ?????????????????????????????????
                    .cacheKey("cacheKey")            // ???????????????????????????key,?????????????????????????????????????????????
                    .cacheMode(CacheMode.DEFAULT)    // ???????????????????????????????????????
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onStart(Request<String, ? extends Request> request) {
                            super.onStart(request);
                            dialog.show();
                        }

                        @Override
                        public void onSuccess(Response<String> response) {
                            String s=response.body();
                            dialog.dismiss();
                            RelationListBean relationListBean = new Gson().fromJson(s, RelationListBean.class);
                            if (relationListBean.isSuccess()){
                                for (int i=0;i<relationListBean.getData().size();i++){
                                    relationList.add(relationListBean.getData().get(i));
                                }
                                mDataGridView.setDataSource(relationList);
                                mDataGridView.updateAll();
                            }
                        }
                    });
        }
    }

    public void confirmOrderForGoods(String ids,String orderId){
        HttpParams params=new HttpParams();
        params.put("ids",ids);
        params.put("orderId",orderId);
        if (BaseApplication.isNetworkAvailable(BusinessRelationActivity.this)) {
            OkGo.<String>post(UrlUtils.CONFIRM_ORDER_FOR_GOODS)     // ?????????????????????url
                    .tag(this)                       // ????????? tag, ?????????????????????????????????
                    .cacheKey("cacheKey")            // ???????????????????????????key,?????????????????????????????????????????????
                    .cacheMode(CacheMode.DEFAULT)    // ???????????????????????????????????????
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String s=response.body();
                            CheckBean checkBean = new Gson().fromJson(s, CheckBean.class);
                            if (checkBean.isSuccess()){
                                if (!mTagIDArrayList.isEmpty()) {
                                    String ids="";
                                    for (int i=0;i<mTagIDArrayList.size();i++){
                                        ids+=getShortTag(mTagIDArrayList.get(i).getEpc())+",";
                                    }
                                    ids=ids.substring(0,ids.length()-1);
                                    getLabelForGoods(ids);
                                }
                            }else {
                                Toast.makeText(BusinessRelationActivity.this,checkBean.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void cancelOrderForGoods(String ids){
        HttpParams params=new HttpParams();
        params.put("ids",ids);
        if (BaseApplication.isNetworkAvailable(BusinessRelationActivity.this)) {
            OkGo.<String>post(UrlUtils.CANCEL_ORDER_FOR_GOODS)     // ?????????????????????url
                    .tag(this)                       // ????????? tag, ?????????????????????????????????
                    .cacheKey("cacheKey")            // ???????????????????????????key,?????????????????????????????????????????????
                    .cacheMode(CacheMode.DEFAULT)    // ???????????????????????????????????????
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String s=response.body();
                            CheckBean checkBean = new Gson().fromJson(s, CheckBean.class);
                            if (checkBean.isSuccess()){
                                if (!mTagIDArrayList.isEmpty()) {
                                    String ids="";
                                    for (int i=0;i<mTagIDArrayList.size();i++){
                                        ids+=getShortTag(mTagIDArrayList.get(i).getEpc())+",";
                                    }
                                    ids=ids.substring(0,ids.length()-1);
                                    getLabelForGoods(ids);
                                }
                            }else {
                                Toast.makeText(BusinessRelationActivity.this,checkBean.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
