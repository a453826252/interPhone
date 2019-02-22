package com.interphone.headset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;


public class BlueToothHeadeSetManager {

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED.equals(action)) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED && mStartAudioRecordCallBack != null && mHandler != null) {
                    mHandler.removeCallbacks(scoFailRunnable);
                    mStartAudioRecordCallBack.success();
                }
            } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
                if (state == BluetoothAdapter.STATE_CONNECTED && isBluetoothHeadsetConnected()) {
                    startAudioRecord(null);
                } else if (state == BluetoothAdapter.STATE_DISCONNECTING ) {
                    stopAudioRecord();
                }
            }
        }
    };

    private static BlueToothHeadeSetManager ourInstance;

    private BluetoothAdapter mBluetoothAdapter;

    private AudioManager mAudioManager;


    public static BlueToothHeadeSetManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new BlueToothHeadeSetManager(context);
        }
        return ourInstance;
    }

    private BlueToothHeadeSetManager(Context context) {
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        if (mBluetoothAdapter == null) {
            BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bm.getAdapter();
        }
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        try {
            context.registerReceiver(mBroadcastReceiver, filter);
        } catch (Exception e) {
            context.unregisterReceiver(mBroadcastReceiver);
            context.registerReceiver(mBroadcastReceiver, filter);
        }
    }

    //sco打开超时时间5s
    private final int timeOutSco = 5000;

    //计时器
    private Handler mHandler;
    private Runnable scoFailRunnable = new Runnable() {
        @Override
        public void run() {
            if (mStartAudioRecordCallBack != null) {
                mStartAudioRecordCallBack.fail("连接超时");
                mHandler = null;
            }
        }
    };

    private startAudioRecordCallBack mStartAudioRecordCallBack;

    public boolean isBluetoothHeadsetConnected() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    public void startAudioRecord(final startAudioRecordCallBack callBack) {
        if (mAudioManager != null) {
            if(mAudioManager.isBluetoothScoOn() && callBack != null){
                callBack.success();
                return;
            }
            mStartAudioRecordCallBack = callBack;
            mAudioManager.setBluetoothScoOn(true);
            mAudioManager.startBluetoothSco();
            mHandler = new Handler();
            mHandler.postDelayed(scoFailRunnable, timeOutSco);
        }
    }

    public void stopAudioRecord() {
        if (mAudioManager != null && mAudioManager.isBluetoothScoOn()) {
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }

    public interface startAudioRecordCallBack {

        void success();

        void fail(String msg);

    }
}
