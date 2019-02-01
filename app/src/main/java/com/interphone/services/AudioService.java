package com.interphone.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.interphone.audio.IAudioRecord;
import com.interphone.audio.impl.AudioRecordManager;
import com.interphone.client.IClient;

public class AudioService extends Service {

    private AudioBinder mAudioBinder = new AudioBinder();
    private IAudioRecord mAudioRecord;


    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBinder;
    }

    public class AudioBinder extends Binder {

        public void initAudioRecord(Activity activity, final IClient client) {
            mAudioRecord = new AudioRecordManager(new AudioRecordManager.IRecordData() {
                @Override
                public void recordData(IAudioRecord record, byte[] data, int size) {
                    client.sendData(data, size);
                }
            }, activity);
        }

        public boolean hasInited() {
            return mAudioRecord != null;
        }

        public void stopRecord() {
            if (mAudioRecord != null) {
                mAudioRecord.stopRecord();
            }
        }

        public void startRecord() {
            if (mAudioRecord != null) {
                mAudioRecord.startRecord();
            }
        }

        public void toggleRecord() {
            if (mAudioRecord != null) {
                if (mAudioRecord.isRecording()) {
                    mAudioRecord.stopRecord();
                } else {
                    mAudioRecord.startRecord();
                }
            }
        }
    }
}
