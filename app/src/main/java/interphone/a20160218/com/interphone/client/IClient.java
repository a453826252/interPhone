package interphone.a20160218.com.interphone.client;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import java.util.List;

public interface IClient {

    void init(Activity activity, Handler handler);

    void sendData();

    void reciveData();

    void connectServer(ScanResult scanResult);

    void disConnectFromServer();

    List<ScanResult> getAllScanResult();
}
