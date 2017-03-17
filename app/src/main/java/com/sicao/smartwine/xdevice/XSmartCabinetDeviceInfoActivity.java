package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xshop.XShopProductInfoActivity;
import com.sicao.smartwine.xuser.XSettingActivity;
import com.sicao.smartwine.xwidget.device.SmartCabinetToolBar;
import com.sicao.smartwine.xwidget.device.xchart.SplineChart03View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class XSmartCabinetDeviceInfoActivity extends SmartCabinetActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    AppBarLayout appBarLayout;
    LinearLayout linearLayout;
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
    //当前设备
    GizWifiDevice mDevice = null;
    //设备灯开关
    boolean isLight = false;
    //正在扫描酒款的进度框
    ProgressBar progressBar;
    //数量统计
    SplineChart03View splineChart03View;
    //酒柜设备按钮
    ImageView mCabinetSetIcon;
    //酒柜灯开关
    ImageView mCabinetLightIcon;
    //
    CoordinatorLayout coordinatorLayout;
    //
    SmartCabinetToolBar smartCabinetToolBar;
    //折叠时显示的温度
    TextView mRealTemp2;
    //当前的RFID数量，增加的RFID数量，移除的RFID数量
    TextView mCurrentRfid, mAddRfid, mRemoveRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int setView() {
        return R.layout.activity_xdevice;
    }

    void init() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.toolbar_layout1);
        mCabinetLightIcon = (ImageView) findViewById(R.id.base_top_device_light);
        mCabinetSetIcon = (ImageView) findViewById(R.id.base_top_device_set);
        mSetTemp = (TextView) findViewById(R.id.set_temp);
        mRealTemp = (TextView) findViewById(R.id.textView11);
        mWorkModel = (TextView) findViewById(R.id.wine_mode);
        mOnLine = (TextView) findViewById(R.id.online);
        mBodys = (TextView) findViewById(R.id.tv_add_wine);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        splineChart03View = (SplineChart03View) findViewById(R.id.splinechart);
        splineChart03View.setLayoutParams(new LinearLayout.LayoutParams(SmartCabinetApplication.metrics.widthPixels, SmartCabinetApplication.metrics.widthPixels * 1 / 3));
        smartCabinetToolBar = (SmartCabinetToolBar) findViewById(R.id.toolbar);
        smartCabinetToolBar.setMinimumHeight(SmartCabinetApplication.metrics.widthPixels / 3);
        mRealTemp2 = (TextView) findViewById(R.id.textView15);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        coordinatorLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mCurrentRfid = (TextView) findViewById(R.id.current_rfids);
        mAddRfid = (TextView) findViewById(R.id.add_rfids);
        mRemoveRfid = (TextView) findViewById(R.id.remove_rfids);
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
            mCabinetLightIcon.setImageResource(R.drawable.ic_bulb_off);
            mSetTemp.setText("0℃");
            mRealTemp.setText("0℃");
            mRealTemp2.setText("0℃");
            mWorkModel.setText("未设置");
            mOnLine.setText("离线");
        }
    }

    @Override
    public void rfidstart() {
        mBodys.setText("正在盘点酒柜...");
    }

    @Override
    public void rfidend() {
        mBodys.setText("酒柜盘点完毕...");
    }

    @Override
    public void rfidbreak() {
        mBodys.setText("酒柜盘点中断...");
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void rfid(GizWifiDevice device, ArrayList<XRfidEntity> current, ArrayList<XRfidEntity> add, ArrayList<XRfidEntity> remove) {
        //1秒后更新RFID数量
        Message msg = handler.obtainMessage();
        msg.what = XConfig.CABINET_INFO_UPDATE_RFIDS_NUMBER;
        msg.arg1 = (current.size() + add.size());
        handler.sendMessageDelayed(msg, 1000);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void refushDeviceList(List<GizWifiDevice> deviceList) {
        for (GizWifiDevice device : deviceList) {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                mCenterTitle.setText(!"".equals(device.getRemark()) ? device.getRemark() : "智能酒柜");
                if (device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline || device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled) {
                    device.setListener(mBindListener);
                    device.setSubscribe(true);
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
        try {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this))) {
                SmartSicaoApi.log("current device is " + device.toString() + "\n" + object.toString());
                //更新设备信息
                isLight = object.getBoolean("light");
                mCabinetLightIcon.setImageResource(isLight ? R.drawable.ic_bulb_on : R.drawable.ic_bulb_off);
                mSetTemp.setText(object.getString("set_temp") + "℃");
                mRealTemp.setText(object.getString("real_temp") + "℃");
                mRealTemp2.setText(object.getString("real_temp") + "℃");
                mWorkModel.setText(getResources().getStringArray(R.array.device_model_show)[object.getInt("model")]);
                mOnLine.setText((mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline || mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled) ? "在线" : "离线");
                if (object.getBoolean("door_open")) {
                    Toast("酒柜门----开");
                }
                if (object.getBoolean("scanning")) {
                    Toast("正在盘点酒柜...");
                    mBodys.setText("正在盘点酒柜...");
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                //先从缓存取出盘点数据
                try {
                    JSONObject object1 = new JSONObject(XUserData.getDefaultCabinetScanRfids(this));
                    if (!object1.isNull("mac") && object1.getString("mac").equals(device.getMacAddress())) {
                        if (!object1.isNull("current")) {
                            //当前的数量
                            mCurrentRfid.setText("当前酒款\n" + (object1.getInt("current") + object1.getInt("add")));
                            mBodys.setText("酒柜内放置" + (object1.getInt("current") + object1.getInt("add")) + "瓶酒");
                        }
                        if (!object1.isNull("add")) {
                            //增加的数量
                            mAddRfid.setText("增加酒数\n+" + object1.getInt("add"));
                        }
                        if (!object1.isNull("remove")) {
                            //减少的数量
                            mRemoveRfid.setText("取出酒数\n-" + object1.getInt("remove"));
                        }
                    }
                } catch (JSONException e) {
                    SmartSicaoApi.log(XSmartCabinetDeviceInfoActivity.class.getSimpleName() + "--获取盘点数据--" + e.getMessage());
                }
                //从服务器获取标签信息
                xSicaoApi.getServerCabinetRfidsByMAC(this, mDevice.getMacAddress(), new XApiCallBack() {
                    @Override
                    public void response(Object object) {
                        //设置相关酒款

                    }
                }, null);

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
            case R.id.base_top_device_set://进入酒柜的设备页面
                if (null != mDevice) {
                    startActivity(new Intent(this, SmartCabinetSettingActivity.class).putExtra("device", mDevice));
                } else {
                    Toast("请选择某一设备后重试!");
                }
                break;
            case R.id.base_top_device_light://设备灯开关
                if (null != mDevice) {
                    xCabinetApi.controlDevice(mDevice, "light", isLight ? false : true, XConfig.CONFIG_CABINET_SET_LIGHT_ACTION);
                } else {
                    Toast("请选择某一设备后重试!");
                }
                break;
            case R.id.setting://设置
//                startActivity(new Intent(this, XSettingActivity.class));
                startActivity(new Intent(this, XShopProductInfoActivity.class).putExtra("productID","1827"));
                break;
//            case R.id.base_top_right_icon://
//                final XWarnDialog dialog = new XWarnDialog(this);
//                dialog.setTitle("退出登录");
//                dialog.setContent("您将要退出该帐号的登录,\n 注意:下次启用需要重新登录!");
//                dialog.show();
//                dialog.setOnListener(new XWarnDialog.OnClickListener() {
//                    @Override
//                    public void makeSure() {
//                        dialog.dismiss();
//                        mHintText.setVisibility(View.VISIBLE);
//                        mHintText.setText("正在退出...");
//                        showProgress(true);
//                        handler.sendEmptyMessageDelayed(10094, 2000);
//                    }
//
//                    @Override
//                    public void cancle() {
//                        dialog.dismiss();
//                    }
//                });
//                break;
            case R.id.my_wines://酒柜内的酒款
                //测试使用
                if (null != mDevice) {
                    startActivity(new Intent(this, SmartCabinetWinesActivity.class).putExtra("cabinet", mDevice));
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
    public void message(Message msg) {
        int what = msg.what;
        if (what == 10094) {
            XUserData.setPassword(XSmartCabinetDeviceInfoActivity.this, "");
            finish();
        } else if (what == XConfig.CURRENT_NO_CABINET) {
            mDevice = null;
            mCabinetLightIcon.setImageResource(R.drawable.ic_bulb_off);
            mSetTemp.setText("0℃");
            mRealTemp.setText("0℃");
            mRealTemp2.setText("0℃");
            mWorkModel.setText("未设置");
            mOnLine.setText("离线");
        } else if (what == XConfig.CABINET_INFO_UPDATE_RFIDS_NUMBER) {
            //
            mBodys.setText("酒柜内放置" + msg.arg1 + "瓶酒");
        } else if (what == XConfig.CABINET_HAS_EXCEPTION) {
            //设备异常状态

        }
    }

    @Override
    public void deviceError(GizWifiDevice device) {
        //设备异常,有可能是设备离线了，或者当前用户是该设备的子帐号，被主帐号远程解绑了,也有可能是设备准备工作异常
        if (null != mDevice && null != device) {
            if (mDevice.getDid().equals(device.getDid())) {
                //当前没有设备进行监控
                handler.sendEmptyMessage(XConfig.CABINET_HAS_EXCEPTION);
            }
        }
    }

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public void onStateChanged(State state) {
        if (state == State.EXPANDED) {
            //展开状态
            linearLayout.setVisibility(View.GONE);
        } else if (state == State.COLLAPSED) {
            //折叠状态
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            //中间状态
            linearLayout.setVisibility(View.GONE);
        }
    }

    private State mCurrentState = State.IDLE;

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
}
