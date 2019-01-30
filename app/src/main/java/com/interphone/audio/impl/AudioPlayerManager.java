package com.interphone.audio.impl;

import android.media.AudioTrack;
import android.util.Log;

import com.dgk.myaudiodemo.util.Speex;
import com.interphone.BaseActivity;
import com.interphone.audio.IAudioConfig;
import com.interphone.audio.IAudioPlayer;

public class AudioPlayerManager implements IAudioPlayer, IAudioConfig {

    private final String TAG = BaseActivity.TAG;

    private AudioTrack mAudioPlayer;

    private Speex mSpeex;

    /**
     * 音频帧数据长度
     * 通常一个音频帧为20ms内的音频数据，
     * 由于 码率 = 8K * 16bit * 1 = 108Kbps = 16KBps，
     * 所以 音频帧数据大小为 int size = 20ms * 16KBps = 320 Byte = 160 Short，
     * 即每个音频帧的数据大小为 320个字节，或者为160个Short。
     */
    private int audioShortArrayLength = 160;

    // 解码后的音频数据
    private short[] decodedShorts = new short[audioShortArrayLength];

    public AudioPlayerManager() {
        mSpeex = new Speex();
        mSpeex.open(Speex.DEFAULT_COMPRESSION);
        init();
    }

    private void init() {
        int playerBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        switch (playerBufferSize) {
            case AudioTrack.ERROR_BAD_VALUE:
                Log.e(TAG, "无效的音频参数");
                break;
            case AudioTrack.ERROR:
                Log.e(TAG, "不能够查询音频输出的性能");
                break;
            default:
                Log.i(TAG, "AudioTrack的音频缓冲区的最小尺寸(与本机硬件有关)：" + playerBufferSize);
                break;
        }
        mAudioPlayer = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, playerBufferSize, defaultMode);
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
    public void play(byte[] data, int size) {
        if (mAudioPlayer != null) {
            int decode = mSpeex.decode(data, decodedShorts, size);
            int write = mAudioPlayer.write(decodedShorts, 0, decode);
            if (write < 0) {
                Log.e(TAG, "write失败");
                switch (write) {
                    case AudioTrack.ERROR_INVALID_OPERATION:
                        Log.e(TAG, "AudioTrack实例初始化失败!");
                        break;
                    case AudioTrack.ERROR_BAD_VALUE:
                        Log.e(TAG, "无效的音频参数");
                        break;
                    case AudioTrack.ERROR:
                        Log.e(TAG, "通用操作失败");
                        break;
                }
            } else {
                Log.i(TAG, "成功写入数据：" + size + " Shorts");
                mAudioPlayer.play();
            }
        }
    }

    @Override
    public void release() {
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer = null;
        }
    }
}
