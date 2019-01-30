package com.interphone.server.impl;

import android.app.Activity;
import android.os.Handler;

import com.interphone.server.ServerAdapter;
import com.interphone.socket.SocketServerReceive;
import com.interphone.wifi.bean.ApConfig;
import com.interphone.wifi.impl.WifiClientManagerImp;

public class ServerImpl extends ServerAdapter {
    private WifiClientManagerImp.AP ap;
    private ApConfig apConfig;


    @Override
    public void shoutDownServer() {
        ap.closeAp(apConfig);
        ap.release();
    }

    @Override
    public boolean closeConnection(int id) {
        return false;
    }

    @Override
    public void openServer(Activity activity, Handler handler, ApConfig apConfig) {
        //开启热点
        ap = WifiClientManagerImp.AP.getInstance(activity, handler);
        this.apConfig = apConfig;
        ap.openAp(apConfig);
    }


}
