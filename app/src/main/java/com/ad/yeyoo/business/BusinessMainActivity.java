package com.ad.yeyoo.business;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ad.yeyoo.R;
import com.ad.yeyoo.base.BaseApplication;
import com.ad.yeyoo.mm.MMMainActivity;
import com.ad.yeyoo.utils.WakeLockUtil;

public class BusinessMainActivity extends Activity {
    private TextView tv_title,tv_saomaguanlian,tv_gongxuliuzhuan,tv_chengpinchuku;
    private AlertDialog gongxuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_main);
        ((BaseApplication) getApplication()).addActivity(this);
        WakeLockUtil.KeepScreenOn(this);//禁止锁屏

        tv_title=findViewById(R.id.tv_title);
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
                        intent.putExtra("gongxuId",(i+1)+"");
                        intent.putExtra("gongxuName",item[i]);
                        startActivity(intent);
                    }
                })
                .create();

        tv_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(BusinessMainActivity.this, MMMainActivity.class);
                startActivity(intent);
                return false;
            }
        });

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
                Intent intent=new Intent(BusinessMainActivity.this, BusinessChukuActivity.class);
                startActivity(intent);
            }
        });
    }


}
