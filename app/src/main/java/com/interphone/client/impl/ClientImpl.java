package com.interphone.client.impl;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import com.interphone.client.IClient;
import com.interphone.datahanding.adapter.DataSendAndReceiveAdapter;
import com.interphone.server.ServerAdapter;
import com.interphone.socket.SocketServerReceive;
import com.interphone.socket.SocketServerSend;
import com.interphone.wifi.IWifiClientManager;
import com.interphone.wifi.impl.WifiClientManagerImp;

import java.util.List;

public class ClientImpl extends DataSendAndReceiveAdapter {
    private IWifiClientManager wifiClientManager;


    @Override
    public void initClient(Activity activity, Handler handler) {
        wifiClientManager = WifiClientManagerImp.getInstance(activity, handler);
        wifiClientManager.openWifi();
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
