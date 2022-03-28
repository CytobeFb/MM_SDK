package com.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ad.demo.base.BaseApplication;
import com.ad.demo.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by endyc on 2018-12-17.
 */

public class PubDemoParaActivity extends Activity {
    // Debugging
    private static final String TAG = "PhychipsDemoParaActivity";
    private static final int REQUEST_ENABLE_TAG = 1;
    public static String EXTRAS_TAG_SAVENAME = "tag_save_list";
    public static String EXTRAS_TAG_OUTUSED = "tag_outused";
    public static String EXTRAS_TAG_OUTTYPE = "tag_outtype";
    // Return Intent extra
    public static String EXTRAS_TAG_OUTSTART = "tag_outstart";
    // Return Intent extra
    public static String EXTRAS_TAG_OUTBYTE = "tag_outbyte";
    private Context mContext;

    private TextView mBaseSetButton;
    private TextView mBaseDefButton;

    private Spinner mOutUsedSpinner;
    private Spinner mOutTypeSpinner;
    private EditText mOutStartEdit;
    private EditText mOutByteEdit;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.demo_para_button_save:

                    Map<String, String> map = new HashMap<String, String>(); //本地保存数据
                    map.put(EXTRAS_TAG_OUTUSED, String.valueOf(mOutUsedSpinner.getSelectedItemPosition()));
                    map.put(EXTRAS_TAG_OUTTYPE, String.valueOf(mOutTypeSpinner.getSelectedItemPosition()));
                    map.put(EXTRAS_TAG_OUTSTART, String.valueOf(mOutStartEdit.getText().toString()));
                    map.put(EXTRAS_TAG_OUTBYTE, String.valueOf(mOutByteEdit.getText().toString()));
                    PreferenceUtil.saveSettingNote(mContext, EXTRAS_TAG_SAVENAME, map);//参数（上下文，userinfo为文件名，需要保存的数据）

                    // Create the result Intent and include the MAC address
                    final Intent intent = new Intent();
                    intent.putExtra(EXTRAS_TAG_OUTUSED, String.valueOf(mOutUsedSpinner.getSelectedItemPosition()));
                    intent.putExtra(EXTRAS_TAG_OUTTYPE, String.valueOf(mOutTypeSpinner.getSelectedItemPosition()));
                    intent.putExtra(EXTRAS_TAG_OUTSTART, String.valueOf(mOutStartEdit.getText().toString()));
                    intent.putExtra(EXTRAS_TAG_OUTBYTE, String.valueOf(mOutByteEdit.getText().toString()));

                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case R.id.demo_para_button_def:
                    mOutUsedSpinner.setSelection(0, true);
                    mOutTypeSpinner.setSelection(1, true);
                    mOutStartEdit.setText("0");
                    mOutByteEdit.setText("12");
                    break;
            }
        }
    };

    private void initUI() {

        mBaseSetButton = (TextView) findViewById(R.id.demo_para_button_save);
        mBaseDefButton = (TextView) findViewById(R.id.demo_para_button_def);
        mBaseSetButton.setOnClickListener(onClickListener);
        mBaseDefButton.setOnClickListener(onClickListener);

        mOutUsedSpinner = (Spinner) findViewById(R.id.demo_para_outused_value);
        mOutTypeSpinner = (Spinner) findViewById(R.id.demo_para_outtype_value);
        mOutStartEdit = (EditText) findViewById(R.id.demo_para_outstart_value);
        mOutByteEdit = (EditText) findViewById(R.id.demo_para_outbyte_value);


        String strUsed = PreferenceUtil.getSettingNote(mContext, EXTRAS_TAG_SAVENAME, EXTRAS_TAG_OUTUSED);
        String strType = PreferenceUtil.getSettingNote(mContext, EXTRAS_TAG_SAVENAME, EXTRAS_TAG_OUTTYPE);
        String strStart = PreferenceUtil.getSettingNote(mContext, EXTRAS_TAG_SAVENAME, EXTRAS_TAG_OUTSTART);
        String strByte = PreferenceUtil.getSettingNote(mContext, EXTRAS_TAG_SAVENAME, EXTRAS_TAG_OUTBYTE);

        if (strUsed == null) strUsed = "0";
        if (strType == null) strType = "1";
        if (strStart == null) strStart = "0";
        if (strByte == null) strByte = "12";

        mOutUsedSpinner.setSelection(Integer.parseInt(strUsed), true);
        mOutTypeSpinner.setSelection(Integer.parseInt(strType), true);
        mOutStartEdit.setText(strStart);
        mOutByteEdit.setText(strByte);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_demo_para);    //需要修改

        mContext = this;
        ((BaseApplication) getApplication()).addActivity(this);
        this.setTitle(R.string.title_shortepc);

        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_TAG && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
