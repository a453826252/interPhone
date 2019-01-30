package com.interphone.wifi;

import com.interphone.wifi.bean.ApConfig;

import java.util.List;

public interface IAPManager {

    void openAp(ApConfig config);

    void closeAp(ApConfig config);

    List<String> getClientIps();
}
