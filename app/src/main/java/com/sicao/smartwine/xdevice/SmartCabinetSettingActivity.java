package com.sicao.smartwine.xdevice;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingType;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingUserRole;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingWay;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xwidget.dialog.SmartCabinetSettingDialog;
import com.sicao.smartwine.xwidget.zxing.ActivityCapture;

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
    ImageView QRcode;
    //添加设备的菜单
    SmartCabinetSettingDialog smartCabinetSettingDialog = null;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_mode_name://工作模式
                workMode = new SmartCabinetSettingDialog(SmartCabinetSettingActivity.this);
                workMode.update(getResources().getStringArray(R.array.device_model_string));
                workMode.showAtLocation(mContent, Gravity.BOTTOM,
                        0, 0);
                workMode.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
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
                workTemp.setHeight(SmartCabinetApplication.metrics.widthPixels * 3 / 5);
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
        init();
        showProgress(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设备状态查询
        xCabinetApi.bindDevice(mDevice, mBindListener);
        xCabinetApi.getDeviceStatus(mDevice);
    }

    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        showProgress(false);
        try {
            SmartSicaoApi.log("current device is " + device.toString() + "\n" + object.toString());
            int model = object.getInt("model");
            mWorkName.setText(getResources().getStringArray(R.array.device_model_string)[model]);
            mWorkTemp.setText(object.getString("set_temp") + "℃");
            wineName.setText(!"".equals(device.getRemark()) ? device.getRemark() : "智能酒柜");
        } catch (JSONException e) {
            SmartSicaoApi.log("the device update data json has error in " + (null == e ? getClass().getSimpleName() : e.getMessage()));
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
        String[] menu = null;
        //鉴定该用户是否有分享该设备的权限
        if(mDevice.getSharingRole() == GizDeviceSharingUserRole.GizDeviceSharingOwner){
            //登录账号为设备的主账号
            menu=new String[]{"分享给好友", "查看绑定账号"};
            mRightText.setVisibility(View.VISIBLE);
        }else if(mDevice.getSharingRole() == GizDeviceSharingUserRole.GizDeviceSharingSpecial){
            //还不是主账号，但是是第一个绑定设备的账号，分享该设备后即将成为主账号
            menu=new String[]{"分享给好友"};
            mRightText.setVisibility(View.VISIBLE);
        }else{
            menu=new String[]{};
            mRightText.setVisibility(View.GONE);
        }
        smartCabinetSettingDialog = new SmartCabinetSettingDialog(this);
        smartCabinetSettingDialog.update(menu);
        smartCabinetSettingDialog.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_device_list_pop_menu_bg));
        smartCabinetSettingDialog.setWidth(smartCabinetSettingDialog.dip2px(this, 150));
        smartCabinetSettingDialog.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        smartCabinetSettingDialog.setMenuItemClickListener(new SmartCabinetSettingDialog.MenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(int position, String value) {
                smartCabinetSettingDialog.dismiss();
                if (position == 0) {
                    //获取该设备的分享信息
                    GizDeviceSharing.getDeviceSharingInfos(XUserData.getCabinetToken(SmartCabinetSettingActivity.this),
                            GizDeviceSharingType.GizDeviceSharingByMe, mDevice.getDid());
                } else {
                      //查看设备所有绑定的账号
                    startActivity(new Intent(SmartCabinetSettingActivity.this,SmartCabinetBindUsersActivity.class).putExtra("GizWifiDevice",mDevice));
                }
            }
        });
        QRcode= (ImageView) findViewById(R.id.qrcode);
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
        mRightText.setText("操作");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!smartCabinetSettingDialog.isShowing())
                    smartCabinetSettingDialog.showLocation(R.id.base_top_right_icon);
                else
                    smartCabinetSettingDialog.dismiss();
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

    @Override
    public void getSharingInfoError(String result) {
        super.getSharingInfoError(result);
        QRcode.setVisibility(View.GONE);
        Toast.makeText(this, "设备分享失败-"+result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getSharingInfoSuccess(String deviceID, int sharingID, Bitmap QRCodeImage) {
        super.getSharingInfoSuccess(deviceID, sharingID, QRCodeImage);
        if (deviceID.equals(mDevice.getDid())){
            //加载该二维码
            QRcode.setVisibility(View.VISIBLE);
            QRcode.setImageBitmap(QRCodeImage);
        }
    }
}

