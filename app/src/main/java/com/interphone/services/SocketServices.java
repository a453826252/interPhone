package com.interphone.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.interphone.client.IClient;
import com.interphone.server.IServer;

public class SocketServices extends Service {

    private IServer mIServer;
    private IClient mIClient;
    private SocketBinder mSocketBinder = new SocketBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mSocketBinder;
    }

    public class SocketBinder extends Binder {
        public void openServerReceiveSocket(IServer server) {
            if (server != null) {
                mIServer = server;
                server.openReceiveDataServer();
            }
        }

        public void toggleServerReceiveSocket(IServer server) {
            if (server != null) {
                mIServer = server;
                if (server.isReceiveServerOpen()) {
                    server.closeReceiveDataServer();
                } else {
                    server.openReceiveDataServer();
                }
            }
        }


        public void closeServerReceiveSocket(IServer server) {
            if (server != null) {
                server.closeReceiveDataServer();
            }
        }

        public void openClientReceiveSocket(IClient client) {
            if (client != null) {
                mIClient = client;
                client.openReceiveDataServer();
            }
        }

        public void toggleClientReceiveSocket(IClient client) {
            if (client != null) {
                mIClient = client;
                if (client.isReceiveServerOpen()) {
                    client.closeReceiveDataServer();
                } else {
                    client.openReceiveDataServer();
                }
            }
        }

        public void closeClientReceiveSocket(IClient client) {
            if (client != null) {
                client.closeReceiveDataServer();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocketBinder.closeClientReceiveSocket(mIClient);
        mSocketBinder.closeServerReceiveSocket(mIServer);
    }
}
