package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;

/***
 * 测试使用，调试标签EPC信息
 */
public class SmartCabinetRFIDActivity extends SmartCabinetActivity {

    TextView rfids;
    int i = 100;
    GizWifiDevice mDevice;
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
    public void rfid(String rfid) {
        rfids.setText("监控中"+rfids.getText().toString().trim().replace("监控中","")+ "\n"+rfid + "\n");
    }

    @Override
    public void message(Message msg) {
        if (msg.what == 10101010) {
            rfids.setText("监控中"+rfids.getText().toString().trim().replace("监控中","") + ".");
        }
    }
}
