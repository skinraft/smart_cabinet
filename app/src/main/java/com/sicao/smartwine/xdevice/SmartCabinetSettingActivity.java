package com.sicao.smartwine.xdevice;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.gizwits.gizwifisdk.api.GizDeviceSharingInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingType;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingUserRole;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingWay;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
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

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

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
    //分享的二维码图片控件
    ImageView QRcode;
    //分享的二维码图片提示
    TextView mQRcodeHint;
    //添加设备的菜单
    SmartCabinetSettingDialog smartCabinetSettingDialog = null;
    //设置读写器工作时间
    EditText scan_time;
    //修改读写器工作时间的提交按钮
    TextView mCommit2;
    //读写器的工作时间
    int serverTime = 0;

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
                        swipeRefreshLayout.setRefreshing(true);
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
                        swipeRefreshLayout.setRefreshing(true);
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
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设备状态查询
        xCabinetApi.bindDevice(mDevice, mBindListener);
        xCabinetApi.getDeviceStatus(mDevice);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (null != mDevice) {
            outState.putParcelable("smart_cabinet_set", mDevice);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (null != savedInstanceState) {
            if (savedInstanceState.containsKey("smart_cabinet_set")) {
                mDevice = (GizWifiDevice) savedInstanceState.get("smart_cabinet_set");
                init();
                swipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        mDevice = device;
        swipeRefreshLayout.setRefreshing(false);
        mCommit.setVisibility(View.GONE);
        mContent2.setVisibility(View.GONE);
        try {
            SmartSicaoApi.log("current device is " + device.toString() + "\n" + object.toString());
            int model = object.getInt("model");
            mWorkName.setText(getResources().getStringArray(R.array.device_model_string)[model]);
            mWorkTemp.setText(object.getString("set_temp") + "℃");
            wineName.setText(!"".equals(device.getRemark()) ? device.getRemark(): "智能酒柜");
            serverTime = object.getInt("scan_time");
            scan_time.setText(serverTime + "");

        } catch (JSONException e) {
            SmartSicaoApi.log("the device update data json has error in " + (null == e ? getClass().getSimpleName() : e.getMessage()));
        }
    }

    @Override
    public void setCustomInfoSuccess(GizWifiDevice device) {
        super.setCustomInfoSuccess(device);
        swipeRefreshLayout.setRefreshing(false);
        xCabinetApi.getDeviceStatus(mDevice);
        Toast("操作成功");
    }

    @Override
    public void setCustomInfoError(String result) {
        super.setCustomInfoError(result);
        swipeRefreshLayout.setRefreshing(false);
        SmartSicaoApi.log("set sustom info error " + result);
        Toast(result.toString());
    }

    public void init() {
        String[] menu = null;
        //鉴定该用户是否有分享该设备的权限
        if (mDevice.getSharingRole() == GizDeviceSharingUserRole.GizDeviceSharingOwner) {
            //登录账号为设备的主账号
            menu = new String[]{"分享给好友", "查看共享账号"};
            mRightText.setVisibility(View.VISIBLE);
        } else if (mDevice.getSharingRole() == GizDeviceSharingUserRole.GizDeviceSharingSpecial) {
            //还不是主账号，但是是第一个绑定设备的账号，分享该设备后即将成为主账号
            menu = new String[]{"分享给好友"};
            mRightText.setVisibility(View.VISIBLE);
        } else {
            menu = new String[]{};
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
                    GizDeviceSharing.sharingDevice(XUserData.getCabinetToken(SmartCabinetSettingActivity.this), mDevice.getDid(), GizDeviceSharingWay.GizDeviceSharingByQRCode,
                            null, GizUserAccountType.GizUserNormal);
                } else {
                    //查看设备所有绑定的账号
                    startActivity(new Intent(SmartCabinetSettingActivity.this, SmartCabinetBindUsersActivity.class).putExtra("GizWifiDevice", mDevice));
                }
            }
        });
        QRcode = (ImageView) findViewById(R.id.qrcode);
        mQRcodeHint = (TextView) findViewById(R.id.qrcode_hint);
        wineName = (EditText) findViewById(R.id.editText);
        mWorkName = (TextView) findViewById(R.id.work_mode_name);
        mWorkTemp = (TextView) findViewById(R.id.work_mode_temp_name);
        scan_time = (EditText) findViewById(R.id.editText5);
        mWorkName.setOnClickListener(this);
        mWorkTemp.setOnClickListener(this);
        mCommit = (TextView) findViewById(R.id.commit);
        mCommit2 = (TextView) findViewById(R.id.scantime_commit);
        //修改设备名称
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    mDevice.setCustomInfo(wineName.getText().toString(), wineName.getText().toString());
                }
            }
        });
        //修改读写器工作时间
        mCommit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice != null) {
                    if (!scan_time.getText().toString().trim().equals(serverTime + "")) {
                        xCabinetApi.controlDevice(mDevice, "scan_time", Integer.parseInt(scan_time.getText().toString().trim()), XConfig.CONFIG_CABINET_SET_WORK_TIME);
                        swipeRefreshLayout.setRefreshing(true);
                    } else {
                        Toast("您设置的时间与上次一样,请重试!");
                    }
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
                String cabinetname = "";
                if (null == mDevice.getRemark()) {
                    cabinetname = "智能酒柜";
                } else {
                    cabinetname = mDevice.getRemark();
                }
                SmartSicaoApi.log("cabinetname=" + cabinetname + ";winename=" + wineName.getText().toString().trim());
                if (!TextUtils.isEmpty(s) && (!wineName.getText().toString().trim().equals(cabinetname))) {
                    mCommit.setVisibility(View.VISIBLE);
                } else {
                    mCommit.setVisibility(View.GONE);
                }
            }
        });

        //读写器工作时间的设定
        scan_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && (!scan_time.getText().toString().trim().equals(serverTime + ""))) {
                    mCommit2.setVisibility(View.VISIBLE);
                } else {
                    mCommit2.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void getSharingInfoError(String result) {
        super.getSharingInfoError(result);
        QRcode.setVisibility(View.GONE);
        mQRcodeHint.setVisibility(View.GONE);
        Toast("获取分享信息失败---" + result.toString());
    }

    @Override
    public void getSharingInfoSuccess(String deviceID, int sharingID, Bitmap QRCodeImage) {
        if (deviceID.equals(mDevice.getDid())) {
            //加载该二维码
            QRcode.setVisibility(View.VISIBLE);
            mQRcodeHint.setVisibility(View.VISIBLE);
            QRcode.setImageBitmap(QRCodeImage);
            //调用分享
            xCabinetApi.shareGizWifiDevice(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    Toast("分享成功");
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Toast("分享中断，请重试");
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    Toast("您已取消分享");
                }
            }, QRCodeImage);
        }
    }
}

