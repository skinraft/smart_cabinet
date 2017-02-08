package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;

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

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.setting_connect://进入设备列表或者进入添加新设备页面
                startActivity(new Intent(this,SmartCabinetDeviceListActivity.class));
                break;
        }
    }

}
