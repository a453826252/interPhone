package interphone.a20160218.com.interphone.audio;

public interface IAudioPlayer {
    public void write(byte[] data,int size);
    public void play();
}
