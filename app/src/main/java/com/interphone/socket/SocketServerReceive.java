package com.interphone.socket;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.interphone.BaseActivity;
import com.interphone.audio.IAudioPlayer;
import com.interphone.audio.impl.AudioPlayerManager;
import com.interphone.socket.impl.SocketMessageImpl;
import com.interphone.wifi.bean.ConnectConfig;
import com.zlandzbt.tools.jv.utils.LogUtils;
import com.zlandzbt.tools.jv.utils.ThreadUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SocketServerReceive {
    private DatagramSocket mDatagramSocket;
    private IAudioPlayer mPlayer;
    private boolean isReceive = true;
    private ISocketMessage mISocketMessage;
    private final String TAG = BaseActivity.TAG;

    public SocketServerReceive() {
        mPlayer = new AudioPlayerManager();
        mISocketMessage = new SocketMessageImpl();
        ThreadUtils.createCacheThreadPool(true, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setDaemon(true);
                thread.setName(thread.getName() + "_receive");
                return thread;
            }
        });
    }

    public void startToReceive() {
        LogUtils.i(TAG, "开启接收socket");
        try {
            if (mDatagramSocket == null) {
                mDatagramSocket = new DatagramSocket(null);
                mDatagramSocket.setReuseAddress(true);
                mDatagramSocket.bind(new InetSocketAddress(ConnectConfig.PORT));
            }
            isReceive = true;
            ThreadUtils.executor(new Runnable() {
                @Override
                public void run() {
                    try {
                        receiveData();
                    } catch (Exception e) {
                        Log.e("对讲机", "数据接收异常");
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeServer() {
        isReceive = false;
        LogUtils.i(TAG, "关闭数据接收服务");
        ThreadUtils.releaseThreadPool(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDatagramSocket != null) {
                    mDatagramSocket = null;
                    mPlayer.release();
                }
            }
        }, 500);
    }

    private void receiveData() throws Exception {

        //大小根据[AudioRecordManager]的[encodedbytes]数组大小决定
        byte[] buf = new byte[20];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (isReceive) {
            mDatagramSocket.receive(packet);
            String remoteAddress = packet.getAddress().getHostAddress();
            if ("127.0.0.1".equalsIgnoreCase(remoteAddress) || "192.168.43.1".equalsIgnoreCase(remoteAddress) || ConnectConfig.LOCAL_IP_String.equalsIgnoreCase(remoteAddress)) {
                //本机发出的，丢弃
                continue;
            }
            LogUtils.i(TAG, "接收到数据");
            byte[] data = packet.getData();
            mPlayer.play(data, data.length);
        }
    }

    public boolean isReceive() {
        return isReceive;
    }
}
