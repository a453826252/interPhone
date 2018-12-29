package interphone.a20160218.com.interphone.server;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerAdapter implements IServer {
    @Override
    public abstract void startServer();

    @Override
    public abstract void shoutDownServer();

    @Override
    public abstract void init(Activity activity, Handler handler);

    @Override
    public abstract void sendData();

    @Override
    public abstract void reciveData();

    @Override
    public void connectServer(ScanResult scanResult) {

    }

    @Override
    public void disConnectFromServer() {

    }

    @Override
    public List<ScanResult> getAllScanResult() {
        return new ArrayList<>();
    }
}
