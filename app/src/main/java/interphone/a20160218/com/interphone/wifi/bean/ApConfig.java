package interphone.a20160218.com.interphone.wifi.bean;

import android.net.wifi.WifiConfiguration;

public class ApConfig {

    private String SSID = "对讲服务";

    private final boolean hidden = false;

    private final int encryption = 4;
    /**
     * 密码
     */
    private String preSharedKey;


    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPreSharedKey() {
        return preSharedKey;
    }

    public void setPreSharedKey(String preSharedKey) {
        this.preSharedKey = preSharedKey;
    }


    public boolean isHidden() {
        return hidden;
    }

    public int getEncryption() {
        int indexOfWPA2_PSK = 4;
        for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
            if (WifiConfiguration.KeyMgmt.strings[i].equals("WPA2_PSK")) {
                indexOfWPA2_PSK = i;
                break;
            }
        }
        return encryption;
    }
}
