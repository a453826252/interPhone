package com.interphone.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

public interface IWifiClientManager {

    public boolean openWifi();

    public boolean closeWifi();

    public boolean startScan();

    public void connect(ScanResult scanResult);

    public List<ScanResult> getScanResult();

    public String getServerIp();

    WifiManager getWifiManagerSystem();
}
