package com.interphone.wifi.bean;

import java.util.List;

public class ConnectConfig {
    /**
     * 服务端IP
     * 现默认往192.168.43.255这个地址做UDP广播
     */
    @Deprecated
    public static String ServerIp = "";

    /**
     * 连接到服务端的所有客户端IP
     * 现默认往192.168.43.255这个地址做UDP广播
     */
    @Deprecated
    public static List<String> ClientIps = null;

    /**
     * 连接端口
     */
    public static final int PORT = 2016;

    /**
     * 本地IP地址,int
     */
    public static int LOCAL_IP = 0;

    /**
     * 本地IP,String   xxx.xxx.xxx.xxx
     */
    public static String LOCAL_IP_String = "";
}
