package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;

import org.json.JSONObject;

import java.util.List;

/***
 * 主页面
 */
public class SmartCabinetDeviceInfoActivity extends SmartCabinetActivity {

    @Override
    protected int setView() {
        return R.layout.activity_device_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //监控默认设备
        if (!"".equals(XUserData.getCurrentCabinetId(this))){
            List<GizWifiDevice>list=xCabinetApi.getCacheDeviceList();
            for (GizWifiDevice device:list){
                if (device.getDid().equals(XUserData.getCurrentCabinetId(this))){
                    xCabinetApi.bindDevice(device,mBindListener);
                }
            }
        }
    }
    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        SmartSicaoApi.log("current device is "+ device.toString() + "\n" + object.toString() );
    }
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.setting_connect://进入设备列表或者进入添加新设备页面
                startActivity(new Intent(this, SmartCabinetDeviceListActivity.class));
                break;
        }
    }
}
