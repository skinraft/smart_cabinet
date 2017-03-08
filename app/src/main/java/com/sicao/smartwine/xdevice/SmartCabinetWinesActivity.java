package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesAdpter;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xuser.XWebActivity;

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
    //实时监控RFID变化
    TextView rfids_text;
    int i=100;
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
        rfids_text= (TextView) findViewById(R.id.rfids_text);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
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
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        mRightText.setText("详情");
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInstalled("com.putaoji.android", SmartCabinetWinesActivity.this) && bindputaoji) {
                    if (bindputaoji){
                        //通过AIDL打开葡萄集商品详情页面
                        try {
                            xInterface.openActivity("1", "1062");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            SmartSicaoApi.log("关联葡萄集异常------"+e.getMessage());
                        }
                    }else{
                        //绑定服务失败,启动该应用
                         AppManager.openApp(SmartCabinetWinesActivity.this,"com.putaoji.android","com.putaoji.android.XIndexActivity");
                    }
                } else {
                    //下载页面
                    startActivity(new Intent(SmartCabinetWinesActivity.this, XWebActivity.class).putExtra("url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.putaoji.android"));
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast("查看商品详情");
        if (isInstalled("com.putaoji.android", this) && bindputaoji) {
            //通过AIDL打开葡萄集商品详情页面
            try {
                xInterface.openActivity("1", mWins.get(position).getProduct().getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            //下载页面
            startActivity(new Intent(this, XWebActivity.class).putExtra("url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.putaoji.android"));
        }
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
        //显示实时监控
        rfids_text.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //隐藏实时监控
        rfids_text.setVisibility(View.GONE);
    }

    @Override
    public void rfid(GizWifiDevice device, ArrayList<XRfidEntity> current, ArrayList<XRfidEntity> add, ArrayList<XRfidEntity> remove) {
       //酒柜内RFID发生变化
        rfids_text.setText("酒柜内标签:总共"+(current.size()+add.size())+"个,增加"+add.size()+"个,减少"+remove.size()+"个");
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
        }else if(msg.what == 10101010) {
            rfids_text.setText("实时监控中" + rfids_text.getText().toString().trim().replace("实时监控中", "") + ".");
            if (rfids_text.getText().toString().trim().contains(".......")){
                rfids_text.setText(rfids_text.getText().toString().trim().replace(".......","."));
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
