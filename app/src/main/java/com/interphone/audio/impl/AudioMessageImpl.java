package com.interphone.audio.impl;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.interphone.audio.IAudioMessage;
import com.zlandzbt.tools.jv.utils.UIUtils;

public class AudioMessageImpl implements IAudioMessage {
    private Activity mActivity;
    private Handler mainLoopHandler;
    public AudioMessageImpl(Activity activity){
        this.mActivity =activity;
        mainLoopHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void startToRecord() {
        mainLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.showToast(mActivity.getApplicationContext(),"开始录制音频");
            }
        });
    }

    @Override
    public void stopRecord() {
        mainLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.showToast(mActivity.getApplicationContext(),"停止录制音频");
            }
        });
    }

    @Override
    public void startPlay() {
        mainLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.showToast(mActivity.getApplicationContext(),"开始播放音频");
            }
        });
    }

    @Override
    public void stopPlay() {
        mainLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                UIUtils.showToast(mActivity.getApplicationContext(),"停止播放音频");
            }
        });
    }
}
