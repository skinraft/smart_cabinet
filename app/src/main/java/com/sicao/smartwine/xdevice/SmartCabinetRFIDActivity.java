package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetRFIDAdapter;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * 测试使用，调试标签EPC信息
 */
public class SmartCabinetRFIDActivity extends SmartCabinetActivity {

    TextView rfids;
    int i = 100;
    GizWifiDevice mDevice;
    ArrayList<XRfidEntity>list=new ArrayList<>();
    SmartCabinetRFIDAdapter adapter;
    ListView listView;
    @Override
    protected int setView() {
        return R.layout.activity_smart_cabinet_rfid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设备状态查询
        xCabinetApi.bindDevice(mDevice, mBindListener);
        xCabinetApi.getDeviceStatus(mDevice);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice = (GizWifiDevice) getIntent().getExtras().get("cabinet");
        rfids = (TextView) findViewById(R.id.rfids);
        listView= (ListView) findViewById(R.id.list);
        adapter=new SmartCabinetRFIDAdapter(this,list);
        listView.setAdapter(adapter);
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
    }
    @Override
    public void rfid(GizWifiDevice device, ArrayList<XRfidEntity> current, ArrayList<XRfidEntity> add, ArrayList<XRfidEntity> remove) {
        ArrayList<XRfidEntity>list=new ArrayList<>();
        list.addAll(current);
        list.addAll(add);
        list.addAll(remove);
        rfids.setText("酒柜内标签:当前"+current.size()+"个,增加"+add.size()+"个,减少"+remove.size()+"个");
        adapter.update(list);
    }
    @Override
    public void message(Message msg) {
        if (msg.what == 10101010) {
            rfids.setText("监控中" + rfids.getText().toString().trim().replace("监控中", "") + ".");
        }
    }
}
