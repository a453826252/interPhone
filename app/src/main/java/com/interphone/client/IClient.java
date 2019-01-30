package com.interphone.client;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import java.util.List;

public interface IClient {

    public void initClient(Activity activity, Handler handler);

    public void sendData(byte[] data,int size);

    public void openReceiveDataServer();

    public boolean isReceiveServerOpen();

    public void closeReceiveDataServer();

    public void connectServer(ScanResult scanResult);

    public void disConnectFromServer();

    public List<ScanResult> getAllScanResult();
}
