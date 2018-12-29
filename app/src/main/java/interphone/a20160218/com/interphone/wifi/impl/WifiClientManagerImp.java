package interphone.a20160218.com.interphone.wifi.impl;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;

import com.zlandzbt.tools.jv.runtime_permissions.permission.PermissionCallBack;
import com.zlandzbt.tools.jv.runtime_permissions.permission.Permissions;
import com.zlandzbt.tools.jv.utils.UIUtils;

import java.util.List;

import interphone.a20160218.com.interphone.MainActivity;
import interphone.a20160218.com.interphone.wifi.IWifiClientManager;
import interphone.a20160218.com.interphone.wifi.IWifiMessage;

public class WifiClientManagerImp implements IWifiClientManager {

    private static final int WEP = 0;
    private static final int WPA = 1;
    private static final int NO_PASS = 2;

    private static WifiClientManagerImp wifiManagerImp = new WifiClientManagerImp();

    private static WifiManager wifiManagerSystem;

    private static Activity mContext;

    private static IWifiMessage iWifiMessage;

    private List<ScanResult> scanResults;

    private ProgressDialog mProgressDialog;

    private static Handler sHandlerToMain;

    private BroadcastReceiver scanResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                // wifi已成功扫描到可用wifi。
                scanResults = wifiManagerSystem.getScanResults();
                if (mProgressDialog != null && scanResults.size() > 0) {
                    mProgressDialog.dismiss();
                }
                sHandlerToMain.sendEmptyMessage(MainActivity.MSG_WIFI_SCAN_FINISH);
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                wifiConnectStateChanged(info);
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1111);
                wifiStateChanged(wifiState);
            }else if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    iWifiMessage.passwordError();
                }
            }
        }
    };


    private WifiClientManagerImp() {
    }

    public static WifiClientManagerImp getInstance(Activity context, Handler handler) {
        mContext = context;
        if (wifiManagerSystem == null) {
            synchronized (WifiClientManagerImp.class) {
                if (wifiManagerSystem == null) {
                    iWifiMessage = new WifiMessageImp(mContext);
                    wifiManagerSystem = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    sHandlerToMain = handler;
                }
            }
        }
        return wifiManagerImp;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManagerSystem.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private void beginConnect(WifiConfiguration config) {
//        String ssid;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            ssid = wifiManagerSystem.getConnectionInfo().getSSID();
//        } else {
//            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            ssid = networkInfo.getExtraInfo();
//        }
//        if (config.SSID.equals(ssid)) {
//            iWifiMessage.wifiHasBeenConnected();
//            return;
//        }
        int netWorkId = config.networkId;
        if (netWorkId == -1) {
            netWorkId = wifiManagerSystem.addNetwork(config);
        }
        boolean issuccess = wifiManagerSystem.enableNetwork(netWorkId, true);
        if (!issuccess) {
            iWifiMessage.wifiConnectFailed();
        }
    }

    private WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = SSID;
        wifiConfig.SSID = "\"".concat(SSID).concat("\"");
        wifiConfig.status = WifiConfiguration.Status.ENABLED;

        // Dependent on the security type of the selected network
        // we set the security settings for the configuration
        if (type == NO_PASS) {
            // No security
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.clear();
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        } else if (type == WPA) {
            //WPA/WPA2 Security
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.preSharedKey = "\"".concat(password).concat("\"");
        } else if (type == WEP) {
            // WEP Security
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            if (getHexKey(password)) {
                wifiConfig.wepKeys[0] = password;
            } else {
                wifiConfig.wepKeys[0] = "\"".concat(password).concat("\"");
            }
            wifiConfig.wepTxKeyIndex = 0;
        }
        return wifiConfig;
    }

    private static boolean getHexKey(String s) {
        if (s == null) {
            return false;
        }

        int len = s.length();
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                continue;
            }
            return false;
        }
        return true;
    }

    private void wifiConnectStateChanged(NetworkInfo info) {
        NetworkInfo.DetailedState state = info.getDetailedState();
        switch (state) {
            case CONNECTING://连接中
                iWifiMessage.wifiDisconnecting();
                break;
            case AUTHENTICATING://正在进行身份验证
                iWifiMessage.wifiAuthenticating();
                break;
            case OBTAINING_IPADDR://正在获取IP地址
                iWifiMessage.wifiObtainingIP();
                break;
            case CONNECTED://已连接
                iWifiMessage.wifiConnected();
                break;
            case SUSPENDED://已暂停
                iWifiMessage.wifiSuspended();
                break;
            case DISCONNECTING://正在断开连接
                iWifiMessage.wifiDisconnecting();
                break;
            case DISCONNECTED://已断开
                iWifiMessage.wifiDisconnected();
                break;
            case FAILED://连接失败
                iWifiMessage.wifiConnectFailed();
                break;
            case BLOCKED://已阻止
                iWifiMessage.wifiBlocked();
                break;
        }
    }

    private void wifiStateChanged(int wifiState) {
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                iWifiMessage.wifiHasBeenClosed();
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                iWifiMessage.wifiIsClosing();
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                iWifiMessage.wifiHasBeenOpened();
                startScan();
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                iWifiMessage.wifiIsOpening();
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                iWifiMessage.unKnownError();

        }
    }

    public void release() {
        iWifiMessage = null;
        mContext.unregisterReceiver(scanResultsReceiver);
        mContext = null;
        wifiManagerSystem = null;
        wifiManagerImp = null;
    }

    @Override
    public boolean openWifi() {
        if (!Permissions.with(mContext).checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Permissions.with(mContext)
                    .permissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0)
                    .callback(new PermissionCallBack() {
                        @Override
                        public void permissionGranted(String permission, int code) {
                            openWifi();
                        }

                        @Override
                        public void permissionReject(boolean shouldShowRational, String permission, int code) {
                            UIUtils.showToast(mContext.getApplicationContext(), "请赋予权限");
                        }

                        @Override
                        public void finish(int errorCode, String msg, int code) {

                        }
                    })
                    .request();
            return false;
        }

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(scanResultsReceiver, filter);
        if (!wifiManagerSystem.isWifiEnabled()) {
            iWifiMessage.openWifi();
            return wifiManagerSystem.setWifiEnabled(true);
        } else if (wifiManagerSystem.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            iWifiMessage.wifiIsOpening();
            return true;
        } else if (wifiManagerSystem.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //iWifiMessage.wifiHasBeenOpened();
            return true;
        } else {
            iWifiMessage.wifiOpenFailed();
        }

        return true;
    }

    @Override
    public boolean closeWifi() {
        if (wifiManagerSystem.isWifiEnabled()) {
            iWifiMessage.closeWifi();
            return wifiManagerSystem.setWifiEnabled(false);
        } else if (wifiManagerSystem.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            iWifiMessage.wifiHasBeenClosed();
            return true;
        } else if (wifiManagerSystem.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            iWifiMessage.wifiIsClosing();
            return true;
        } else {
            iWifiMessage.closeWifiFailed();
            return false;
        }
    }

    @Override
    public boolean startScan() {
        boolean scan = wifiManagerSystem.startScan();
        if (scan) {
            mProgressDialog = iWifiMessage.wifiScan();
        } else {
            iWifiMessage.wifiScanFaild();
        }
        return scan;
    }

    @Override
    public void connect(final ScanResult scanResult) {
        int cap;
        wifiManagerSystem.disconnect();
        WifiConfiguration existConfig = isExist(scanResult.SSID);
        if (existConfig == null) {
            String capabilities = scanResult.capabilities.toLowerCase();
            if (capabilities.contains("wpa")) {
                cap = WPA;
            } else if (capabilities.contains("wep")) {
                cap = WEP;
            } else {
                cap = NO_PASS;
            }
            if (cap != NO_PASS) {
                final EditText editText = new EditText(mContext);
                editText.setHint("请输入密码");
                final int capf = cap;
                UIUtils.alertDialogWithView(mContext, editText, new UIUtils.IAlertDialogCallBack() {
                    @Override
                    public void yes(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        String pass = editText.getText().toString();
                        if (TextUtils.isEmpty(pass)) {
                            iWifiMessage.passwordShouldNotBeEmpty();
                            return;
                        }
                        WifiConfiguration config = createWifiInfo(scanResult.SSID, pass, capf);
                        beginConnect(config);
                    }

                    @Override
                    public void no(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
            } else {
                existConfig = createWifiInfo(scanResult.SSID, "", cap);
                beginConnect(existConfig);
            }

        } else {
            beginConnect(existConfig);
        }
    }

    @Override
    public List<ScanResult> getScanResult() {
        return scanResults;
    }
}
