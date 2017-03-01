package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xuser.XSettingActivity;
import com.sicao.smartwine.xwidget.device.RingView;
import com.sicao.smartwine.xwidget.dialog.XWarnDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/***
 * 主页面
 */
public class SmartCabinetDeviceInfoActivity extends SmartCabinetActivity implements View.OnClickListener, RingView.AnimListener {
    //设备灯开关
    ImageView mLight;
    //设置温度
    TextView mSetTemp;
    //实际温度
    TextView mRealTemp;
    //工作模式
    TextView mWorkModel;
    //酒柜内有多少瓶酒
    TextView mBodys;
    //设备是否在线
    TextView mOnLine;
    //动画效果
    RingView mRingView;
    //酒柜设置按钮
    TextView wineSetting;
    //设备信息面板控件
    RelativeLayout mDeviceInfoLayout;
    //同步控件
    RelativeLayout mSynLayout;
    //当前设备
    GizWifiDevice mDevice = null;
    //设备灯开关
    boolean isLight = false;
    //正在同步...控件
    TextView mSynText;

    @Override
    protected int setView() {
        return R.layout.activity_device_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLight = (ImageView) findViewById(R.id.imageView3);
        mSetTemp = (TextView) findViewById(R.id.set_temp);
        mRealTemp = (TextView) findViewById(R.id.textView11);
        mRingView = (RingView) findViewById(R.id.anim_start);
        mWorkModel = (TextView) findViewById(R.id.wine_mode);
        wineSetting = (TextView) findViewById(R.id.textView13);
        wineSetting.setOnClickListener(this);
        mDeviceInfoLayout = (RelativeLayout) findViewById(R.id.lr_setting);
        mSynLayout = (RelativeLayout) findViewById(R.id.syn_layout);
        mOnLine = (TextView) findViewById(R.id.online);
        mSynText = (TextView) findViewById(R.id.syn_text);
        mBodys = (TextView) findViewById(R.id.tv_add_wine);
        mRingView.setAnimListener(this);
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("EXIT");
        mRightText.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //监控上次保存的默认设备
        if (!"".equals(XUserData.getCurrentCabinetId(this))) {
            //获取绑定的设备列表
            xCabinetApi.refushDeviceList(XUserData.getCabinetUid(this), XUserData.getCabinetToken(this), getProductKey());
        } else {
            //当前没有设备进行监控
            mDevice = null;
            mLight.setImageResource(R.drawable.ic_bulb_off);
            mSetTemp.setText("0℃");
            mRealTemp.setText("0℃");
            mWorkModel.setText("未设置");
            mOnLine.setText("离线");
            mSynText.setText("没有设备");
        }
    }

    @Override
    public void refushDeviceList(List<GizWifiDevice> deviceList) {
        for (GizWifiDevice device : deviceList) {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                mCenterTitle.setText(!"".equals(device.getRemark()) ? device.getRemark() : device.getProductName());
                if (device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline || device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled) {
                    mRingView.startAnim();
                    device.setListener(mBindListener);
                    device.setSubscribe(true);
                    mSynText.setText("正在同步...");
                    xCabinetApi.getDeviceStatus(device);
                    GizWifiSDK.sharedInstance().getDevicesToSetServerInfo();
                } else {
                    Toast("目标设备处于不可监控状态");
                }
            }
        }
    }

    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        mDevice = device;
        mRingView.stopAnim();
        try {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                SmartSicaoApi.log("current device is " + device.toString() + "\n" + object.toString());
                //更新设备信息
                isLight = object.getBoolean("light");
                mLight.setImageResource(isLight ? R.drawable.ic_bulb_on : R.drawable.ic_bulb_off);
                mSetTemp.setText(object.getString("set_temp") + "℃");
                mRealTemp.setText(object.getString("real_temp") + "℃");
                mWorkModel.setText(getResources().getStringArray(R.array.device_model_show)[object.getInt("model")]);
                mOnLine.setText(object.getBoolean("isOnline") ? "在线" : "离线");
            }
        } catch (JSONException e) {
            Toast("数据异常,请检查!");
            SmartSicaoApi.log("the device update data json has error in " + (null == e ? getClass().getSimpleName() : e.getMessage()));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.setting_connect://进入设备列表或者进入添加新设备页面
                startActivity(new Intent(this, SmartCabinetDeviceListActivity.class));
                break;
            case R.id.textView13://进入酒柜的设备页面
                if (null != mDevice) {
                    startActivity(new Intent(this, SmartCabinetSettingActivity.class).putExtra("device", mDevice));
                } else {
                    Toast("请选择某一设备后重试!");
                }
                break;
            case R.id.imageView3://设备灯开关
                if (null != mDevice) {
                    xCabinetApi.controlDevice(mDevice, "light", isLight ? false : true, XConfig.CONFIG_CABINET_SET_LIGHT_ACTION);
                } else {
                    Toast("请选择某一设备后重试!");
                }
                break;
            case R.id.setting://设置
                startActivity(new Intent(SmartCabinetDeviceInfoActivity.this, XSettingActivity.class));
                break;
            case R.id.base_top_right_icon://
                final XWarnDialog dialog = new XWarnDialog(this);
                dialog.setTitle("退出登录");
                dialog.setContent("您将要退出该帐号的登录,\n 注意:下次启用需要重新登录!");
                dialog.show();
                dialog.setOnListener(new XWarnDialog.OnClickListener() {
                    @Override
                    public void makeSure() {
                        dialog.dismiss();
                        mHintText.setVisibility(View.VISIBLE);
                        mHintText.setText("正在退出...");
                        showProgress(true);
                        handler.sendEmptyMessageDelayed(10094, 2000);
                    }

                    @Override
                    public void cancle() {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.my_wines://酒柜内的酒款
                if (null != mDevice) {
                    startActivity(new Intent(SmartCabinetDeviceInfoActivity.this, SmartCabinetWinesActivity.class).putExtra("cabinet", mDevice));
                } else {
                    Toast("请选择某一设备后重试!");
                }
                break;
        }
    }

    @Override
    public void setCustomInfoSuccess(GizWifiDevice device) {
        super.setCustomInfoSuccess(device);
        xCabinetApi.getDeviceStatus(mDevice);
        Toast("操作成功");
    }

    @Override
    public void setCustomInfoError(String result) {
        super.setCustomInfoError(result);
        Toast("请重试!");
    }

    @Override
    public void animStart() {
        wineSetting.setVisibility(View.GONE);
        mDeviceInfoLayout.setVisibility(View.GONE);
        mSynLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void animStop() {
        handler.sendEmptyMessageDelayed(10093, 2000);
    }

    @Override
    public void message(Message msg) {
        int what = msg.what;
        if (what == 10093) {
            mDeviceInfoLayout.setVisibility(View.VISIBLE);
            wineSetting.setVisibility(View.VISIBLE);
            mSynLayout.setVisibility(View.GONE);
        } else if (what == 10094) {
            XUserData.setPassword(SmartCabinetDeviceInfoActivity.this, "");
            finish();
        }
    }

}
