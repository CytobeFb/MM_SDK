package com.ad.yeyoo.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ad.rcp.RcpBase;
import com.ad.sio.SioBase;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class BaseApplication extends Application {

    private SioBase mSioBase = null;
    private RcpBase mRcpBase = new RcpBase();
    private List<Activity> activities = new ArrayList<Activity>();

    public SioBase getSio() {
        return this.mSioBase;
    }

    public void setSio(SioBase sio) {
        this.mSioBase = sio;
    }

    public RcpBase getRcp() {
        return this.mRcpBase;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        initOkGo();
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Exception e) {
                ;
            }
        }


        if (mSioBase != null) mSioBase.disConnect();
        mSioBase = null;

        if (mRcpBase != null) mRcpBase = null;
        System.exit(0);
    };

    public void initOkGo(){
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor  loggingInterceptor = new HttpLoggingInterceptor("OkGo");
//log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
//log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
//-------------------------------------------------------------------------------------//

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)           ;                    //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

    /**
     * 检查当前网络是否可用的方法
     *
     * @param context
     *            传入一个上下文对象
     * @return 可用返回true 不可用返回false
     */
    public static boolean isNetworkAvailable(Context context) {
        /**
         * 获取手机所有的连接对象（包括wifi，net等连接的管理）
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取networkinfo对象
            NetworkInfo[] netWorkinfo = connectivityManager.getAllNetworkInfo();
            if (netWorkinfo != null && netWorkinfo.length > 0) {
                for (int i = 0; i < netWorkinfo.length; i++) {
                    /**
                     * 判断已有的网络对象是否处于连接状态 连接状态则返回true 否则返回false
                     */
                    if (netWorkinfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
