package com.interphone.server;

import android.app.Activity;
import android.os.Handler;

import com.interphone.client.IClient;
import com.interphone.wifi.bean.ApConfig;

public interface IServer extends IClient {

    void startServer();

    void shoutDownServer();

    void dataTransform();

    boolean closeConnection(int id);

    void initServer(Activity activity, Handler handler, ApConfig apConfig);
}
