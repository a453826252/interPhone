package com.interphone.datahanding.adapter;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import com.interphone.client.IClient;
import com.interphone.socket.SocketServerReceive;
import com.interphone.socket.SocketServerSend;

import java.util.List;

public class DataSendAndReceiveAdapter implements IClient {

    private SocketServerReceive mSocketServerReceive;
    private SocketServerSend mSocketServerSend;

    @Override
    public void initClient(Activity activity, Handler handler) {

    }

    @Override
    public void sendData(byte[] data, int size) {
        if (mSocketServerSend == null) {
            mSocketServerSend = new SocketServerSend();
        }
        mSocketServerSend.sendData(data, size);
    }

    @Override
    public void openReceiveDataServer() {
        mSocketServerReceive = new SocketServerReceive();
        mSocketServerReceive.startToReceive();
    }

    @Override
    public boolean isReceiveServerOpen() {
        if (mSocketServerReceive != null) {
            return mSocketServerReceive.isReceive();
        }
        return false;
    }

    @Override
    public void closeReceiveDataServer() {
        if (mSocketServerReceive != null) {
            mSocketServerReceive.closeServer();
        }
    }

    @Override
    public void connectServer(ScanResult scanResult) {

    }

    @Override
    public void disConnectFromServer() {

    }

    @Override
    public List<ScanResult> getAllScanResult() {
        return null;
    }
}
