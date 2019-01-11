package interphone.a20160218.com.interphone.server;

import android.app.Activity;
import android.os.Handler;

import interphone.a20160218.com.interphone.client.IClient;
import interphone.a20160218.com.interphone.wifi.bean.ApConfig;

public interface IServer extends IClient {

    void startServer();

    void shoutDownServer();

    void dataTransform();

    boolean closeConnection(int id);

    void initServer(Activity activity, Handler handler, ApConfig apConfig);
}
