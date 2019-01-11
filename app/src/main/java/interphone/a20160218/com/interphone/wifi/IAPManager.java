package interphone.a20160218.com.interphone.wifi;

import interphone.a20160218.com.interphone.wifi.bean.ApConfig;

public interface IAPManager {

    void openAp(ApConfig config);

    void closeAp(ApConfig config);
}
