package com.interphone.socket;

import com.interphone.BaseActivity;
import com.interphone.wifi.bean.ConnectConfig;
import com.zlandzbt.tools.jv.utils.LogUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketServerSend {
    private DatagramSocket mDatagramSocket;
    private final String TAG = BaseActivity.TAG;

    public void sendData(byte[] data, int size) {
        try {
            if (mDatagramSocket == null) {
                mDatagramSocket = new DatagramSocket();
            }
            DatagramPacket packet = new DatagramPacket(data, size, InetAddress.getByName("192.168.43.255"), ConnectConfig.PORT);
            mDatagramSocket.send(packet);
            LogUtils.i(TAG, "发送数据:" + data.length);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, "发送数据异常:" + e.getLocalizedMessage());
        }
    }
}
