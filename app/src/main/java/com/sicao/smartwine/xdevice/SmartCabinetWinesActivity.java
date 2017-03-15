package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesAdpter;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.ArrayList;

public class SmartCabinetWinesActivity extends SmartCabinetActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    //当前酒柜
    GizWifiDevice gizWifiDevice;
    //酒款列表数据
    ArrayList<XWineEntity> mWins = new ArrayList<>();
    //酒款列表适配器
    SmartCabinetWinesAdpter smartCabinetWinesAdpter;
    //列表控件
    ListView listView;
    //回到顶部按钮
    FloatingActionButton floatingActionButton;
    int page = 1;
    int i = 100;
    //酒柜名称
    TextView mCabinetName;
    //酒柜连接状态
    TextView mCabinetNetStatus;
    //酒柜内酒款同步的时间
    TextView mUpdateTime;
    //酒柜内的酒款数量
    TextView mCabinetWinesNum;

    @Override
    protected int setView() {
        return R.layout.activity_smart_cabinet_wines;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init() {
        gizWifiDevice = (GizWifiDevice) getIntent().getExtras().get("cabinet");
        mCabinetWinesNum = (TextView) findViewById(R.id.rfids_text);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        mCabinetName = (TextView) findViewById(R.id.smart_cabinet_wines_name);
        mUpdateTime= (TextView) findViewById(R.id.refesh_time);
        mCenterTitle.setText(!"".equals(gizWifiDevice.getRemark()) ? gizWifiDevice.getRemark() : "智能酒柜");
        mCabinetNetStatus = (TextView) findViewById(R.id.smart_cabinet_wines_statue);
        mCabinetNetStatus.setText((gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline || gizWifiDevice.getNetStatus()
                == GizWifiDeviceNetStatus.GizDeviceControlled) ? "酒柜状态: 已连接" : "酒柜状态: 离线");
        /////////////////////////
        smartCabinetWinesAdpter = new SmartCabinetWinesAdpter(this, mWins);
        listView.setAdapter(smartCabinetWinesAdpter);
        ////////////////////////
        new Thread() {
            @Override
            public void run() {
                while (i > 0) {
                    handler.sendEmptyMessage(10101010);
                    i--;
                    if (i == 1) {
                        i = 100;
                    }
                    Thread.currentThread();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast("查看商品详情");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab://回到顶部
                listView.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsList(page);
        //设备状态查询
        xCabinetApi.bindDevice(gizWifiDevice, mBindListener);
        xCabinetApi.getDeviceStatus(gizWifiDevice);
    }

    @Override
    public void rfid(GizWifiDevice device, ArrayList<XRfidEntity> current, ArrayList<XRfidEntity> add, ArrayList<XRfidEntity> remove) {
        //酒柜内RFID发生变化
        mCabinetWinesNum.setText("当前储藏酒款: " + (current.size() + add.size()));
    }

    @Override
    public void rfidstart() {
        super.rfidstart();
    }

    @Override
    public void message(Message msg) {
        if (msg.what == XConfig.BASE_UPDATE_ACTION) {
            //刷新
            page = 1;
            getGoodsList(page);
        } else if (msg.what == XConfig.BASE_LOAD_ACTION) {
            //加载更多
            page++;
            getGoodsList(page);
        } else if (msg.what == 10101010) {
            mUpdateTime.setText("监控中" + mUpdateTime.getText().toString().trim().replace("监控中", "") + ".");
            if (mUpdateTime.getText().toString().trim().contains("....")) {
                mUpdateTime.setText(mUpdateTime.getText().toString().trim().replace("....", "."));
            }
        }
    }

    public void getGoodsList(final int page) {
        xSicaoApi.getGoodsByMac(this, gizWifiDevice.getMacAddress(), page, new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                if (page == 1) {
                    mWins = (ArrayList<XWineEntity>) list;
                } else {
                    mWins.addAll((ArrayList<XWineEntity>) list);
                }
                smartCabinetWinesAdpter.upDataAdapter(mWins);
                if (mWins.size() > 10) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        }, null);
    }
}
