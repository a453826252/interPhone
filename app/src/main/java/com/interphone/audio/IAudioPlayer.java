package com.interphone.audio;

public interface IAudioPlayer {
    public void play(byte[] data, int size);
    public void release();
}
