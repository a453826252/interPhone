package com.interphone.server.socket;

import android.media.AudioRecord;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServer {
    private final int PORT = 2016;

    public void openServerSocket(){
        try {
            ServerSocket  serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
