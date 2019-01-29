package com.interphone.wifi;

import android.app.ProgressDialog;

public interface IWifiMessage {

    /**************wifi连接*******************/
    void wifiConnectFailed();

    void wifiConnecting();

    /**
     * wifi连接时身份验证
     */
    void wifiAuthenticating();

    void wifiObtainingIP();

    void wifiConnected();

    void wifiHasBeenConnected();

    /**
     * wifi连接被暂停
     */
    void wifiSuspended();

    void wifiDisconnecting();

    void wifiDisconnected();

    /**
     * wifi连接被阻止
     */
    void wifiBlocked();

    /*************开启wifi*************/
    void openWifi();

    void wifiIsOpening();

    void wifiHasBeenOpened();

    void wifiOpenFailed();

    /***************关闭wifi**************/
    void closeWifi();

    void wifiIsClosing();

    void wifiHasBeenClosed();

    void closeWifiFailed();

    /**
     * ===============扫描================
     *
     * @return
     */
    ProgressDialog wifiScan();

    void wifiScanFaild();

    /**
     * ===============密码================
     */
    void passwordShouldNotBeEmpty();

    void passwordError();

    /**
     * ===============其他================
     */
    void unKnownError();

    /**
     * ===============热点================
     */
    void openApSuccess();

    void closeApSuccess();

    void openApFailed();

    void closeApFailed();
}
