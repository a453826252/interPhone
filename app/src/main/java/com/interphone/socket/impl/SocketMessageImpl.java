package com.interphone.socket.impl;

import android.app.Activity;

import com.interphone.socket.ISocketMessage;

public class SocketMessageImpl implements ISocketMessage {

    @Override
    public void onMessage(int id, String msg) {
        switch (id) {
            case START_DATA_RECEIVE_SERVER:
                openReceiveServer();
                break;
            case CLOSE_DATA_RECEIVE_SERCER:
                closeReceiveServer();
                break;
            case DATA_RECEIVE_EXCEPTION:
                dataReceiveException();
                break;
            case DATA_SEND_EXCEPTION:
                dataSendException();
                break;
            default:
        }
    }

    private void openReceiveServer() {
    }

    private void closeReceiveServer() {
    }

    private void dataSendException() {
    }

    private void dataReceiveException() {
    }
}
