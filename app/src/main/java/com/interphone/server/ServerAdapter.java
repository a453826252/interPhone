package com.interphone.server;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import com.interphone.datahanding.adapter.DataSendAndReceiveAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerAdapter extends DataSendAndReceiveAdapter implements IServer {

    @Override
    public void initClient(Activity activity, Handler handler) {

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
