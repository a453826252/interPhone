package com.interphone.server;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerAdapter implements IServer {

    @Override
    public void initClient(Activity activity, Handler handler) {

    }

    @Override
    public void sendData() {
    }

    @Override
    public void reciveData() {
    }

    @Override
    public void connectServer(ScanResult scanResult) {

    }

    @Override
    public void disConnectFromServer() {

    }

    @Override
    public List<ScanResult> getAllScanResult() {
        return new ArrayList<>();
    }


}
