package com.interphone.client.impl;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import java.util.List;

import com.interphone.client.IClient;
import com.interphone.wifi.IWifiClientManager;
import com.interphone.wifi.impl.WifiClientManagerImp;

public class ClientImpl implements IClient {
    private IWifiClientManager wifiClientManager;

    @Override
    public void initClient(Activity activity, Handler handler) {
        wifiClientManager = WifiClientManagerImp.getInstance(activity, handler);
        wifiClientManager.openWifi();
    }

    @Override
    public void sendData() {

    }

    @Override
    public void reciveData() {

    }

    @Override
    public void connectServer(ScanResult scanResult) {
        wifiClientManager.connect(scanResult);
    }

    @Override
    public void disConnectFromServer() {

    }

    @Override
    public List<ScanResult> getAllScanResult() {
        return wifiClientManager.getScanResult();
    }
}
