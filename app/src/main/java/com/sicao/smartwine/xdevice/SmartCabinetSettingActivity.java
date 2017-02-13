package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xwidget.dialog.SmartCabinetSettingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/***
 * 酒柜设置
 */
public class SmartCabinetSettingActivity extends SmartCabinetActivity implements View.OnClickListener {
    //酒柜名称
    EditText wineName;
    //工作模式名称
    TextView mWorkName;
    //设置温度
    TextView mWorkTemp;
    //工作模式
    SmartCabinetSettingDialog workMode;
    //设置温度
    SmartCabinetSettingDialog workTemp;
    //修改酒柜名称时显示的提交按钮
    TextView mCommit;
    //将要控制的设备对象
    GizWifiDevice mDevice;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_mode_name://工作模式
                workMode = new SmartCabinetSettingDialog(SmartCabinetSettingActivity.this);
                workMode.update(getResources().getStringArray(R.array.device_model_string));
                workMode.showAtLocation(mContent, Gravity.BOTTOM,
                        0, 0);
                workMode.setMenuItemClickListener(new SmartCabinetSettingDialog.MenuItemClickListener() {
                    @Override
                    public void onClick(int position, String value) {
                        mWorkName.setText(getResources().getStringArray(R.array.device_model_string)[position]);
                        workMode.dismiss();
                        //工作模式对应的数值
                        int device_model_int = getResources().getIntArray(R.array.device_model_int)[position];
                        //工作模式对应的温度
                        int device_set_temp = 0;
                        if (position == 0) {
                            //手动模式
                            device_set_temp = Integer.parseInt(mWorkTemp.getText().toString().trim().replace("℃", ""));
                        } else {
                            //其他模式
                            device_set_temp = getResources().getIntArray(R.array.device_model_temp)[position];
                        }
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("model", device_model_int);
                        map.put("set_temp", device_set_temp);
                        //更新设备的工作模式
                        xCabinetApi.controlDevice(mDevice, map, XConfig.CONFIG_CABINET_MODEL_TEMP_ACTION);
                        showProgress(true);
                    }
                });
                break;
            case R.id.work_mode_temp_name://设置的温度
                if (!mWorkName.getText().toString().trim().contains("手动")) {
                    Toast.makeText(this, "该工作模式下不可调整温度", Toast.LENGTH_SHORT).show();
                    return;
                }
                workTemp = new SmartCabinetSettingDialog(SmartCabinetSettingActivity.this);
                workTemp.update(getResources().getStringArray(R.array.device_temp));
                workTemp.showAtLocation(mContent, Gravity.BOTTOM,
                        0, 0);
                workTemp.setMenuItemClickListener(new SmartCabinetSettingDialog.MenuItemClickListener() {
                    @Override
                    public void onClick(int position, String value) {
                        workTemp.dismiss();
                        int device_set_temp = Integer.parseInt(getResources().getStringArray(R.array.device_temp)[position]);
                        //更新设备的设置温度
                        xCabinetApi.controlDevice(mDevice, "set_temp", device_set_temp, XConfig.CONFIG_CABINET_SET_TEMP_ACTION);
                        showProgress(true);
                    }
                });
                break;
        }
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_smart_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice = (GizWifiDevice) getIntent().getExtras().get("device");
        xCabinetApi.bindDevice(mDevice,mBindListener);
        init();
        showProgress(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //设备状态查询
        xCabinetApi.getDeviceStatus(mDevice);
    }

    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        showProgress(false);
        try {
            int model = object.getInt("model");
            mWorkName.setText(getResources().getStringArray(R.array.device_model_string)[model]);
            mWorkTemp.setText(object.getString("set_temp") + "℃");
            wineName.setText(!"".equals(device.getRemark()) ? device.getRemark() : "智能酒柜");
        } catch (JSONException e) {
            SmartSicaoApi.log("the device update data json has error " + e.toString());
        }
    }

    @Override
    public void setCustomInfoSuccess(GizWifiDevice device) {
        super.setCustomInfoSuccess(device);
        showProgress(false);
        xCabinetApi.getDeviceStatus(mDevice);
        Toast.makeText(this, "操作成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCustomInfoError(String result) {
        super.setCustomInfoError(result);
        showProgress(false);
        SmartSicaoApi.log("set sustom info error " + result);
        Toast.makeText(this, "设备信息修改失败,请重试", Toast.LENGTH_LONG).show();
    }

    public void init() {
        wineName = (EditText) findViewById(R.id.editText);
        mWorkName = (TextView) findViewById(R.id.work_mode_name);
        mWorkTemp = (TextView) findViewById(R.id.work_mode_temp_name);
        mWorkName.setOnClickListener(this);
        mWorkTemp.setOnClickListener(this);
        mCommit = (TextView) findViewById(R.id.commit);
        //修改设备名称
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice != null) {
                    showProgress(true);
                    mDevice.setCustomInfo(wineName.getText().toString(), wineName.getText().toString());
                }
            }
        });
        //设备名称输入框的监听事件
        wineName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mCommit.setVisibility(View.VISIBLE);
                } else {
                    mCommit.setVisibility(View.GONE);
                }
            }
        });
    }
}

