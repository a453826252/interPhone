package interphone.a20160218.com.interphone;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import interphone.a20160218.com.interphone.client.IClient;
import interphone.a20160218.com.interphone.client.impl.ClientImpl;
import interphone.a20160218.com.interphone.datahanding.adapter.ScanResultAdapter;
import interphone.a20160218.com.interphone.server.IServer;
import interphone.a20160218.com.interphone.server.impl.ServerImpl;
import interphone.a20160218.com.interphone.wifi.bean.ApConfig;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;

    private RecyclerView mScanResultList;

    private FloatingActionButton mFab;

    private NavigationView mNavigationView;

    private IClient mClient;

    private IServer mServer;

    private ScanResultAdapter adapter;

    public final static int MSG_WIFI_SCAN_FINISH = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WIFI_SCAN_FINISH:
                    adapter.getData().clear();
                    adapter.addAll(mClient.getAllScanResult());
                    mScanResultList.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        mScanResultList = f(R.id.scanResultList);
        mFab = f(R.id.fab);
        mDrawer = f(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        mNavigationView = f(R.id.nav_view);

    }

    private void initEvent() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                mServer.initServer(this, handler, config);
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


}
