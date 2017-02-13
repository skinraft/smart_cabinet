package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xwidget.device.RingView;

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
    GizWifiDevice mDevice;
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
        mSynText= (TextView) findViewById(R.id.syn_text);
        mRingView.setAnimListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //监控上次保存的默认设备
        if (!"".equals(XUserData.getCurrentCabinetId(this))) {
            //获取绑定的设备列表
            xCabinetApi.refushDeviceList(XUserData.getCabinetUid(this), XUserData.getCabinetToken(this), getProductKey());
        }else{
            mSynText.setText("没有设备");
        }
    }

    @Override
    public void refushDeviceList(List<GizWifiDevice> deviceList) {
        for (GizWifiDevice device : deviceList) {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                mRingView.startAnim();
                device.setListener(mBindListener);
                device.setSubscribe(true);
                mSynText.setText("正在同步...");
            }
        }
    }

    @Override
    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
        mDevice = device;
        mRingView.stopAnim();
        SmartSicaoApi.log("current device is " + device.toString() + "\n" + object.toString());
        try {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                //更新设备信息
                isLight = object.getBoolean("light");
                mLight.setImageResource(isLight ? R.drawable.ic_bulb_on : R.drawable.ic_bulb_off);
                mSetTemp.setText(object.getString("set_temp") + "℃");
                mRealTemp.setText(object.getString("real_temp") + "℃");
                mWorkModel.setText(getResources().getStringArray(R.array.device_model_show)[object.getInt("model")]);
                mOnLine.setText(object.getBoolean("isOnline") ? "在线" : "离线");
            }
        } catch (JSONException e) {
            SmartSicaoApi.log("the device update data json has error " + e.toString());
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
                startActivity(new Intent(this, SmartCabinetSettingActivity.class).putExtra("device", mDevice));
                break;
            case R.id.imageView3://设备灯开关
                if (null != mDevice) {
                    xCabinetApi.controlDevice(mDevice, "light", isLight ? false : true, XConfig.CONFIG_CABINET_SET_LIGHT_ACTION);
                }
                break;
        }
    }

    @Override
    public void setCustomInfoSuccess(GizWifiDevice device) {
        super.setCustomInfoSuccess(device);
        xCabinetApi.getDeviceStatus(mDevice);
        Toast.makeText(this, "操作成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCustomInfoError(String result) {
        super.setCustomInfoError(result);
        Toast.makeText(this, "请重试!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void animStart() {
        wineSetting.setVisibility(View.GONE);
        mDeviceInfoLayout.setVisibility(View.GONE);
        mSynLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void animStop() {
        mDeviceInfoLayout.setVisibility(View.VISIBLE);
        wineSetting.setVisibility(View.VISIBLE);
        mSynLayout.setVisibility(View.GONE);
    }
}
