package com.interphone.socket;

public interface ISocketMessage {
    int START_DATA_RECEIVE_SERVER = 0;
    int DATA_RECEIVE_EXCEPTION = 1;
    int CLOSE_DATA_RECEIVE_SERCER = 2;
    int DATA_SEND_EXCEPTION = 3;

    void onMessage(int id, String msg);
}
