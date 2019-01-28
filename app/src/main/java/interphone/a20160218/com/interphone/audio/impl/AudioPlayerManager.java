package interphone.a20160218.com.interphone.audio.impl;

import android.media.AudioTrack;
import android.util.Log;

import interphone.a20160218.com.interphone.audio.IAudioConfig;
import interphone.a20160218.com.interphone.audio.IAudioPlayer;

public class AudioPlayerManager implements IAudioPlayer, IAudioConfig {

    private final String TAG = "【AudioPlayerManager】";

    private AudioTrack mAudioPlayer;

    private int mPlayerBufferSize;

    public AudioPlayerManager() {
        init();
    }

    private void init() {
        mPlayerBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        switch (mPlayerBufferSize) {
            case AudioTrack.ERROR_BAD_VALUE:
                Log.e(TAG, "无效的音频参数");
                break;
            case AudioTrack.ERROR:
                Log.e(TAG, "不能够查询音频输出的性能");
                break;
            default:
                Log.i(TAG, "AudioTrack的音频缓冲区的最小尺寸(与本机硬件有关)：" + mPlayerBufferSize);
                break;
        }
        mAudioPlayer = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, mPlayerBufferSize * 4, defaultMode);
        switch (mAudioPlayer.getState()) {
            case AudioTrack.STATE_INITIALIZED:
                Log.i(TAG, "AudioTrack实例初始化成功!");
                break;
            case AudioTrack.STATE_NO_STATIC_DATA:
                Log.i(TAG, "AudioTrack实例初始化成功，目前没有静态数据输入!");
                break;
            default:
                Log.e(TAG, "AudioTrack实例初始化失败!");
        }

    }

    @Override
    public void write(byte[] data, int size) {
        if (mAudioPlayer != null) {
            int write = mAudioPlayer.write(data, 0, size);
            if (write < 0) {
                Log.e(TAG, "write失败");
                switch (write) {
                    case AudioTrack.ERROR_INVALID_OPERATION:    // -3
                        Log.e(TAG, "AudioTrack实例初始化失败!");
                        break;
                    case AudioTrack.ERROR_BAD_VALUE:            // -2
                        Log.e(TAG, "无效的音频参数");
                        break;
                    case AudioTrack.ERROR:                      // -1
                        Log.e(TAG, "通用操作失败");
                        break;
                }
            } else {
                Log.i(TAG, "成功写入数据：" + size + " Shorts");
            }
        }
    }

    @Override
    public void play() {
        if (mAudioPlayer != null) {
            mAudioPlayer.play();
        }
    }
}
