package interphone.a20160218.com.interphone.wifi;

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

    /***************扫描信号************/
    ProgressDialog wifiScan();

    void wifiScanFaild();

    /**********password****************/
    void passwordShouldNotBeEmpty();

    void passwordError();

    /**********other****************/
    void unKnownError();



}
