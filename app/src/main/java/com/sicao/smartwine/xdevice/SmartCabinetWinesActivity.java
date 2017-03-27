package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesAdpter;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xshop.XShopProductInfoActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    //酒柜名称
    TextView mCabinetName;
    //酒柜连接状态
    TextView mCabinetNetStatus;
    //酒柜内酒款同步的时间
    TextView mUpdateTime;
    //酒柜内的酒款数量
    TextView mCabinetWinesNum;
    // 是否允许主动盘点酒柜
    boolean enable = false;
    //酒柜门是否已打开
    boolean door_open = false;

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
        mCabinetName = (TextView) findViewById(R.id.smart_cabinet_wines_name);
        mUpdateTime = (TextView) findViewById(R.id.refesh_time);
        mCabinetName.setText(!"".equals(gizWifiDevice.getRemark()) ? gizWifiDevice.getRemark() : "智能酒柜");
        mCabinetNetStatus = (TextView) findViewById(R.id.smart_cabinet_wines_statue);
        mCabinetNetStatus.setText((gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline || gizWifiDevice.getNetStatus()
                == GizWifiDeviceNetStatus.GizDeviceControlled) ? "酒柜状态: 已连接" : "酒柜状态: 离线");
        /////////////////////////
        smartCabinetWinesAdpter = new SmartCabinetWinesAdpter(this, mWins);
        listView.setAdapter(smartCabinetWinesAdpter);
        ////////////////////////
        /**
         * item点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(SmartCabinetWinesActivity.this, XShopProductInfoActivity.class).putExtra("productID", mWins.get(position).getProduct().getId()));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast("查看商品详情");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设备状态查询
        xCabinetApi.bindDevice(gizWifiDevice, mBindListener);
        xCabinetApi.getDeviceStatus(gizWifiDevice);
        //从服务器获取标签信息
        xSicaoApi.getServerCabinetRfidsByMAC(this, gizWifiDevice.getMacAddress(), new XApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    //设置相关酒款
                    JSONObject object1 = (JSONObject) object;
                    mCabinetWinesNum.setText("总共:" + object1.getString("num") + "瓶，放入:" + object1.getString("newCount") + "瓶，取出:" + object1.getInt("deleteCount") + "瓶");
                    mUpdateTime.setText(object1.getString("date"));
                } catch (JSONException e) {
                    SmartSicaoApi.log(XSmartCabinetDeviceInfoActivity.class.getSimpleName() + "--获取盘点数据--" + e.getMessage());
                }
            }
        }, null);
    }

    @Override
    public void rfid(GizWifiDevice device, ArrayList<XRfidEntity> current, ArrayList<XRfidEntity> add, ArrayList<XRfidEntity> remove) {
        //酒柜内RFID发生变化
        mCabinetWinesNum.setText("总共:" + (current.size() + add.size()) + "瓶，放入:" + add.size() + "瓶，取出:" + remove.size() + "瓶");
        //刷新酒款信息
        getGoodsList();
    }

    @Override
    public void setCustomInfoSuccess(GizWifiDevice device) {
        //主动盘点参数调整OK

    }

    @Override
    public void setCustomInfoError(String result) {
        //主动盘点参数调整失败

    }

    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        try {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                enable = object.getBoolean("scanning");
                door_open = object.getBoolean("door_open");
                if (enable) {
                    Toast("正在盘点酒柜...");
                }
                getGoodsList();
            }
        } catch (JSONException e) {
            Toast("数据异常,请检查!");
            SmartSicaoApi.log("the device update data json has error in " + (null == e ? getClass().getSimpleName() : e.getMessage()));
        }
    }

    @Override
    public void rfidstart() {
        super.rfidstart();
    }

    @Override
    public void message(Message msg) {
        if (msg.what == XConfig.BASE_UPDATE_ACTION) {
            //刷新
            getGoodsList();
            if (!enable && !door_open) {
                xCabinetApi.controlDevice(gizWifiDevice, "scanning", "true", XConfig.CABINET_OPEN_SCANNING);
            }
        }
    }

    public void getGoodsList() {
        xSicaoApi.getGoodsByMac(this, gizWifiDevice.getMacAddress(), new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                mWins = (ArrayList<XWineEntity>) list;
                smartCabinetWinesAdpter.upDataAdapter(mWins);
            }
        }, null);
    }
}
