package interphone.a20160218.com.interphone.audio.impl;

import android.media.AudioRecord;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zlandzbt.tools.jv.utils.ThreadUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import interphone.a20160218.com.interphone.audio.IAudioConfig;
import interphone.a20160218.com.interphone.audio.IAudioRecord;

public class AudioRecordManager implements IAudioConfig, IAudioRecord {

    private static final String TAG = "【AudioRecordManager】";

    private int mRecordBufferSize;

    private AudioRecord mAudioRecord;

    private boolean mIsBusy = false;

    private IRecordData mIRecordData;

    public AudioRecordManager(IRecordData recordData) {
        this.mIRecordData = recordData;
        init();
        ThreadUtils.createCacheThreadPool(false,new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setName(thread.getName() + "-AudioRecord");
                return thread;
            }
        });
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
        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mRecordBufferSize * 4);
        switch (mAudioRecord.getState()) {
            case AudioRecord.STATE_INITIALIZED:
                Log.i(TAG, "获取录音实例成功");
                break;
            default:
                Log.e(TAG, "获取录音实例失败");
        }
    }

    @Override
    public void startRecord() {
        if (mIsBusy) {
            Log.e(TAG, "请等待当前录制结束");
            return;
        }
        mIsBusy = true;
        ThreadUtils.executor(new Runnable() {
            private byte[] recordData = new byte[mRecordBufferSize];

            @Override
            public void run() {
                while (true) {
                    int len = mAudioRecord.read(recordData, 0, recordData.length);
                    mIRecordData.recordData(recordData, len);
                }
            }
        });
    }

    @Override
    public void stopReord() {
        ThreadUtils.releaseThreadPool(true);
    }

    static interface IRecordData {
        public void recordData(byte[] data, int size);
    }
}
