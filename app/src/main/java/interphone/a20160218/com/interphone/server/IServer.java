package interphone.a20160218.com.interphone.server;

import interphone.a20160218.com.interphone.client.IClient;

public interface IServer extends IClient {

    void startServer();

    void shoutDownServer();

}
