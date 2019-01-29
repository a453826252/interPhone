package com.interphone.wifi;

import com.interphone.wifi.bean.ApConfig;

public interface IAPManager {

    void openAp(ApConfig config);

    void closeAp(ApConfig config);
}
