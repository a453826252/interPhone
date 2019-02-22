package com.interphone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.interphone.client.IClient;
import com.interphone.client.impl.ClientImpl;
import com.interphone.datahanding.adapter.ScanResultAdapter;
import com.interphone.server.IServer;
import com.interphone.server.impl.ServerImpl;
import com.interphone.services.AudioService;
import com.interphone.services.SocketServices;
import com.interphone.wifi.bean.ApConfig;
import com.interphone.wifi.bean.ConnectConfig;
import com.zlandzbt.tools.jv.utils.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawer;

    private RecyclerView mScanResultList;

    private RelativeLayout tips;

    private FloatingActionButton mFab;

    private NavigationView mNavigationView;

    private ScanResultAdapter adapter;

    private AudioService.AudioBinder mAudioBinder;

    private SocketServices.SocketBinder mSocketBinder;

    private IServer mServer;

    private IClient mClient;

    public final static int MSG_WIFI_SCAN_FINISH = 0;

    public static final int MSG_OPEN_AP_SUCCESS = 1;

    public static final int MSG_CLOSE_AP_SUCCESS = 2;

    public static final int MSG_CONNECT_SUCCESS = 3;

    public static final int MSG_DISCONNECT_SUCCESS = 4;

    private Handler handler;

    private BroadcastReceiver wifiReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    getLocalIp();
                }
            }
        }
    };

    private ServiceConnection audioConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioBinder = (AudioService.AudioBinder) service;
        }
    };

    private ServiceConnection socketConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSocketBinder = (SocketServices.SocketBinder) service;
        }
    };
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this, AudioService.class), audioConnection, 0);
        bindService(new Intent(this, SocketServices.class), socketConnection, 0);
        startService(new Intent(this, AudioService.class));
        startService(new Intent(this, SocketServices.class));
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        initView();
        initEvent();
    }

    private void getLocalIp() {
        ConnectConfig.LOCAL_IP = mWifiManager.getConnectionInfo().getIpAddress();
        ConnectConfig.LOCAL_IP_String = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ConnectConfig.LOCAL_IP & 0xff), (ConnectConfig.LOCAL_IP >> 8 & 0xff), (ConnectConfig.LOCAL_IP >> 16 & 0xff), (ConnectConfig.LOCAL_IP >> 24 & 0xff));
    }

    private void initView() {
        mScanResultList = f(R.id.scanResultList);
        tips = f(R.id.tipsContainer);
        mFab = f(R.id.fab);
        mDrawer = f(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        mNavigationView = f(R.id.nav_view);

    }

    private void initEvent() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_WIFI_SCAN_FINISH:
                        tips.setVisibility(View.GONE);
                        mScanResultList.setVisibility(View.VISIBLE);
                        adapter.getData().clear();
                        adapter.addAll(mClient.getAllScanResult());
                        mScanResultList.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    case MSG_OPEN_AP_SUCCESS:
                        UIUtils.showAlertMessage(MainActivity.this, "提示", "热点开启成功，点击右下角按钮开启监听，请告知你的同伴连接至该热点");
                        break;
                    case MSG_CLOSE_AP_SUCCESS:
                    case MSG_DISCONNECT_SUCCESS:
                        mSocketBinder.closeClientReceiveSocket(mClient);
                    case MSG_CONNECT_SUCCESS:
                        mSocketBinder.openClientReceiveSocket(mClient);
                        break;

                    default:
                }
            }
        };


        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onFabClick();

            }
        });
        mNavigationView.setNavigationItemSelectedListener(this);
        mClient = new ClientImpl();
        mServer = new ServerImpl();
        mScanResultList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScanResultAdapter(this, new ArrayList<ScanResult>(), new ScanResultAdapter.IOnItemClick<ScanResult>() {
            @Override
            public void onClick(ScanResult item) {
                mClient.connectServer(item);
            }

            @Override
            public void onLongClick(ScanResult item) {

            }
        });
        mScanResultList.setAdapter(adapter);

        mScanResultList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (mWifiManager.isWifiEnabled()) {
            getLocalIp();
        }
        registerReceiver(wifiReceive, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
    }

    private void onFabClick() {
        mSocketBinder.toggleClientReceiveSocket(mClient);
        if (!mAudioBinder.hasInited()) {
            mAudioBinder.initAudioRecord(this, mClient);
        }
        mAudioBinder.toggleRecord();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.server:
                ApConfig config = new ApConfig();
                config.setSSID("对讲服务");
                config.setPreSharedKey("12345678");
                mServer.openServer(this, handler, config);
                break;
            case R.id.client:
                mClient.initClient(this, handler);
                break;
            case R.id.setting:
                break;
            case R.id.share:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceive);

    }
}
