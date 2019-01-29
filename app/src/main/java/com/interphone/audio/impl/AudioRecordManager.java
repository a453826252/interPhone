package com.interphone.audio.impl;

import android.media.AudioRecord;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dgk.myaudiodemo.util.Speex;
import com.interphone.audio.IAudioConfig;
import com.interphone.audio.IAudioRecord;
import com.zlandzbt.tools.jv.utils.ThreadUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AudioRecordManager implements IAudioConfig, IAudioRecord {

    private static final int SPEEX_DATA_SIZE = 20;

    private static final String TAG = "【AudioRecordManager】";

    private int mRecordBufferSize;

    private AudioRecord mAudioRecord;

    private boolean mIsBusy = false, isWorking = true;

    private IRecordData mIRecordData;

    private Speex mSpeex;

    /**
     * 音频帧数据长度
     * 通常一个音频帧为20ms内的音频数据，
     * 由于 码率 = 8K * 16bit * 1 = 108Kbps = 16KBps，
     * 所以 音频帧数据大小为 int size = 20ms * 16KBps = 320 Byte = 160 Short，
     * 即每个音频帧的数据大小为 320个字节，或者为160个Short。
     */
    private int audioShortArrayLength = 160;

    public AudioRecordManager(IRecordData recordData) {
        this.mIRecordData = recordData;
        mSpeex = new Speex();
        mSpeex.open(Speex.DEFAULT_COMPRESSION);
        init();
    }

    private void init() {
        mRecordBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        switch (mRecordBufferSize) {
            case AudioRecord.ERROR_BAD_VALUE:
                Log.e(TAG, "参数有误");
                break;
            case AudioRecord.ERROR:
                Log.e(TAG, "不能查询音频输入性能");
                break;
            default:
                Log.i(TAG, "尺寸:" + mRecordBufferSize);
        }
        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mRecordBufferSize);
        switch (mAudioRecord.getState()) {
            case AudioRecord.STATE_INITIALIZED:
                Log.i(TAG, "获取录音实例成功");
                break;
            default:
                Log.e(TAG, "获取录音实例失败");
        }

        ThreadUtils.createCacheThreadPool(false, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setName(thread.getName() + "-AudioRecord");
                return thread;
            }
        });
    }

    @Override
    public void startRecord() {
        if (mIsBusy) {
            Log.e(TAG, "请等待当前录制结束");
            return;
        }
        if (mAudioRecord == null) {
            init();
        }
        isWorking = true;
        mIsBusy = true;
        ThreadUtils.executor(new Runnable() {
            private short[] recordData = new short[audioShortArrayLength];
            private byte[] encodedbytes = new byte[SPEEX_DATA_SIZE];

            @Override
            public void run() {
                mAudioRecord.startRecording();
                while (isWorking) {
                    if (mAudioRecord == null) {
                        Log.e(TAG, "mAudioRecord为null");
                        continue;
                    }
                    int len = mAudioRecord.read(recordData, 0, recordData.length);
                    if (len > 0) {
                        Log.e(TAG, "读取到的语音数据的长度：" + len + " Shorts");
                        // 语音压缩-对音频数据编码
                        int l = mSpeex.encode(recordData, 0, encodedbytes, len);
                        //处理音频数据
                        mIRecordData.recordData(AudioRecordManager.this, encodedbytes, l);
                    } else {
                        switch (len) {
                            case AudioRecord.ERROR_INVALID_OPERATION:
                                Log.e(TAG, "读取语音信息...发现实例初始化失败！");
                                break;
                            case AudioRecord.ERROR_BAD_VALUE:
                                Log.e(TAG, "读取语音信息...发现参数无效！");
                                break;
                            default:
                                Log.e(TAG, "读取语音信息...错误！" + len);
                        }
                    }

                }
            }
        });
    }

    @Override
    public void stopReord() {
        isWorking = false;
        ThreadUtils.releaseThreadPool(true);
        mIsBusy = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.stop();
                mAudioRecord.release();


                mAudioRecord = null;
            }
        }, 500);

    }

    public static interface IRecordData {
        public void recordData(IAudioRecord record, byte[] data, int size);
    }

    @Override
    public boolean isRecording() {
        return mIsBusy;
    }
}
