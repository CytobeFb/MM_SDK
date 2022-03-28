package com.ad.demo.business;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ad.demo.R;
import com.ad.demo.base.BaseApplication;
import com.ad.demo.utils.WakeLockUtil;

public class BusinessMainActivity extends Activity {
    private TextView tv_saomaguanlian,tv_gongxuliuzhuan,tv_chengpinchuku;
    private AlertDialog gongxuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_main);
        ((BaseApplication) getApplication()).addActivity(this);
        WakeLockUtil.KeepScreenOn(this);//禁止锁屏

        tv_saomaguanlian=findViewById(R.id.tv_saomaguanlian);
        tv_gongxuliuzhuan=findViewById(R.id.tv_gongxuliuzhuan);
        tv_chengpinchuku=findViewById(R.id.tv_chengpinchuku);

        final String[] item = new String[]{"织片","检验","洗缩","灯检","整烫","成检","入箱"};//创建item
        gongxuDialog = new AlertDialog.Builder(BusinessMainActivity.this)
                .setTitle("选择工序")
                .setItems(item, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(BusinessMainActivity.this, BusinessGongxuActivity.class);
                        intent.putExtra("gongxuId",item[i]);
                        startActivity(intent);
                    }
                })
                .create();

        tv_saomaguanlian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BusinessMainActivity.this, BusinessRelationActivity.class);
                startActivity(intent);
            }
        });

        tv_gongxuliuzhuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gongxuDialog.show();
            }
        });

        tv_chengpinchuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
