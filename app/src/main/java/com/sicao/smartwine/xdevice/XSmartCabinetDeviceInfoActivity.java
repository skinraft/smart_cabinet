package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesHistoryAdpter;
import com.sicao.smartwine.xdevice.entity.XProductHistoryEntity;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xuser.XSettingActivity;
import com.sicao.smartwine.xwidget.device.SmartCabinetToolBar;
import com.sicao.smartwine.xwidget.device.xchart.SplineChart03View;

import org.json.JSONException;
import org.json.JSONObject;
import org.xclcharts.chart.PointD;

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
    //酒柜内有多少支酒
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
    //历史记录
    ListView list_view;
    //历史记录的适配器
    SmartCabinetWinesHistoryAdpter smartCabinetWinesHistoryAdpter;
    //历史记录的数据
    ArrayList<XProductHistoryEntity> historyEntities = new ArrayList<>();

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
        list_view = (ListView) findViewById(R.id.list_view);
        smartCabinetWinesHistoryAdpter = new SmartCabinetWinesHistoryAdpter(this, historyEntities);
        list_view.setAdapter(smartCabinetWinesHistoryAdpter);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (list_view != null && list_view.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = list_view.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = list_view.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
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
            mBodys.setText("总共:" + 0 + "支，放入:" + 0 + "支，取出:" + 0 + "支");
            ArrayList<PointD> linePoint1 = new ArrayList<>();
            splineChart03View.chartDataSet(linePoint1);
            splineChart03View.invalidate();
            historyEntities = new ArrayList<>();
            smartCabinetWinesHistoryAdpter.upDataAdapter(historyEntities);
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
        msg.arg2 = remove.size();
        msg.obj = add.size();
        handler.sendMessageDelayed(msg, 1000);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void refushDeviceList(List<GizWifiDevice> deviceList) {
        for (GizWifiDevice device : deviceList) {
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this)) && device.isBind()) {
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
            if (device.getDid().equals(XUserData.getCurrentCabinetId(this)) && device.isBind()) {
                //
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
                    //通知从服务器拉取rfid数据
                    handler.sendEmptyMessageAtTime(XConfig.CABINET_GET_RFIDS_ALL_ADD_REMOVE_ACTION, 2000);
                }
                //通知从服务器拉取统计数据
                handler.sendEmptyMessageAtTime(XConfig.CABINET_GET_STATISTICS_ALL_ADD_REMOVE_ACTION, 2000);
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
                startActivity(new Intent(this, XSettingActivity.class));
                break;
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

    public void openHistory(View view) {
        if (null != mDevice) {
            startActivity(new Intent(this, SmartCabinetHistoryActivity.class).putExtra("mac", mDevice.getMacAddress()));
        } else {
            Toast("请选择某一设备后重试!");
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
            mBodys.setText("总共:" + 0 + "支，放入:" + 0 + "支，取出:" + 0 + "支");
            ArrayList<PointD> linePoint1 = new ArrayList<>();
            splineChart03View.chartDataSet(linePoint1);
            splineChart03View.invalidate();
            historyEntities = new ArrayList<>();
            smartCabinetWinesHistoryAdpter.upDataAdapter(historyEntities);
        } else if (what == XConfig.CABINET_INFO_UPDATE_RFIDS_NUMBER) {
            //msg.arg1 = (current.size() + add.size());
            mBodys.setText("总共:" + msg.arg1 + "支，放入:" + msg.obj + "支，取出:" + msg.arg2 + "支");
        } else if (what == XConfig.CABINET_HAS_EXCEPTION) {
            //设备异常状态

        } else if (what == XConfig.CABINET_GET_RFIDS_ALL_ADD_REMOVE_ACTION) {
            //
            notifyRfid();
        } else if (what == XConfig.CABINET_GET_STATISTICS_ALL_ADD_REMOVE_ACTION) {
            notifyStatistics();
        }
    }

    public void notifyRfid() {
        //从服务器获取标签信息
        xSicaoApi.getServerCabinetRfidsByMAC(this, mDevice.getMacAddress(), new XApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    //设置相关酒款
                    JSONObject object1 = (JSONObject) object;
                    mBodys.setText("总共:" + object1.getInt("all") + "支，放入:" + object1.getString("add") + "支，取出:" + object1.getInt("remove") + "支");
                } catch (JSONException e) {
                    SmartSicaoApi.log(XSmartCabinetDeviceInfoActivity.class.getSimpleName() + "--获取盘点数据--" + e.getMessage());
                }
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                mBodys.setText("总共:" + 0 + "支，放入:" + 0 + "支，取出:" + 0 + "支");
            }
        });
    }

    public void notifyStatistics() {
        //从服务器获取当月统计信息
        xSicaoApi.getstatistics(this, mDevice.getMacAddress(), new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                ArrayList<PointD> linePoint1 = (ArrayList<PointD>) list;
                splineChart03View.chartDataSet(linePoint1);
                splineChart03View.invalidate();
            }
        }, null);
        //历史记录
        getHistory(mDevice.getMacAddress());
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

    /***
     * 获取该设备的历史记录
     * @param mac
     */
    public void getHistory(String mac) {
        xSicaoApi.getCurHistoryByMac(this, mac, new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                historyEntities = (ArrayList<XProductHistoryEntity>) list;
                smartCabinetWinesHistoryAdpter.upDataAdapter(historyEntities);
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                historyEntities.clear();
                smartCabinetWinesHistoryAdpter.upDataAdapter(historyEntities);
            }
        });
    }
}
