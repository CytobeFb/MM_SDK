package com.ad.yeyoo.utils;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.ad.yeyoo.R;

import java.util.Map;

/**
 * Created by endyc on 2016-03-09.
 */
public class SoundPoolUtil {
    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    public static Context context;
    protected static float audioCurrentVolume;

    protected static int playId = 0;
    protected static int soundId = 0;
    protected static Boolean isLoaded = false;

    /**
     * 初始化声音池
     *
     * @param context
     */
    public static void initSoundPool(Context context) {
        SoundPoolUtil.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(5);
            sp = builder.build();

            /*
            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .build();*/

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool sound, int sampleId, int status) {
                isLoaded = true;
            }
        });

        soundId = sp.load(context, R.raw.msg, 1);

        AudioManager am = (AudioManager) SoundPoolUtil.context.getSystemService(SoundPoolUtil.context.AUDIO_SERVICE);
        //返回当前AlarmManager最大音量
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //返回当前AudioManager对象的音量值
        audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //float volumnRatio = audioCurrentVolume/audioMaxVolume;
    }

    //放声音池声音
    public static void play(final int number) {

        if (isLoaded == true) {
            sp.play(soundId,
                    1,// 左声道音量
                    1,// 右声道音量
                    1, // 优先级
                    number,// 循环播放次数
                    1);// 回放速度，该值在0.5-2.0之间 1为正常速度
        }
    }
}
