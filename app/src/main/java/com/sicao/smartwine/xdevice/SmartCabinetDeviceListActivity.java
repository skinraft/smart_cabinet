package com.sicao.smartwine.xdevice;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetDeviceAdapter;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenu;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuCreator;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuItem;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuListView;
import com.sicao.smartwine.xwidget.dialog.SmartCabinetSettingDialog;
import com.sicao.smartwine.xwidget.dialog.XWarnDialog;
import com.sicao.smartwine.xwidget.refresh.SwipeRefreshLayout;
import com.sicao.smartwine.xwidget.zxing.ActivityCapture;

import java.util.ArrayList;
import java.util.List;

/***
 * 我的酒柜列表数据
 */
public class SmartCabinetDeviceListActivity extends SmartCabinetActivity {
    //设备列表
    SwipeMenuListView mDeviceListView;
    //设备列表数据源
    List<GizWifiDevice> mListData = new ArrayList<GizWifiDevice>();
    //数据列表适配器
    SmartCabinetDeviceAdapter mAdapter;
    //添加设备的菜单
    SmartCabinetSettingDialog smartCabinetSettingDialog = null;
    //
    GizWifiDevice gizWifiDevice=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取缓存的设备列表数据
        initDate(xCabinetApi.getCacheDeviceList());
    }

    public void initDate(List<GizWifiDevice> deviceList) {
        mListData = deviceList;
        mAdapter.update(mListData);
    }

    @Override
    public void refushDeviceList(List<GizWifiDevice> deviceList) {
        initDate(deviceList);
    }

    @Override
    public void update(boolean update, String action) {
        //刷新设备列表
        initDate(xCabinetApi.getCacheDeviceList());
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_list;
    }

    public void init() {
        String[] menu = new String[]{"扫码添加设备", "配置新设备"};
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
                    //扫码添加设备
                    //判断权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) || !checkPermission(Manifest.permission.CAMERA)) {
                            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 10086);
                        } else {
                            startActivity(new Intent(SmartCabinetDeviceListActivity.this, ActivityCapture.class));
                        }
                    } else {
                        startActivity(new Intent(SmartCabinetDeviceListActivity.this, ActivityCapture.class));
                    }
                } else {
                    //配置新设备
                    startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetConfigActivity.class));
                }
            }
        });
        mDeviceListView = (SwipeMenuListView) findViewById(R.id.view4);
        mAdapter = new SmartCabinetDeviceAdapter(this, mListData);
        mDeviceListView.setAdapter(mAdapter);
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("菜单");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!smartCabinetSettingDialog.isShowing())
                    smartCabinetSettingDialog.showLocation(R.id.base_top_right_icon);
                else
                    smartCabinetSettingDialog.dismiss();
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.remove);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mDeviceListView.setMenuCreator(creator);
        mDeviceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                //"设备解绑后您将不再拥有该设备,请谨慎操作!"
                gizWifiDevice = mListData.get(position);
                if (gizWifiDevice.isBind()) {
                    //已经绑定的设备才可以处理解绑动作
                    bindDevice(gizWifiDevice, false);
                } else {
                    //表逗我，没绑定解绑你妹哦
                    Toast.makeText(SmartCabinetDeviceListActivity.this, "亲,您还没有绑定该设备哦", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Item的点击事件
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gizWifiDevice = mListData.get(position);
                if (!gizWifiDevice.isBind()) {
                    //没有绑定的设备，执行绑定设备，并为其设置监听并订阅
                    bindDevice(gizWifiDevice, true);
                    SmartSicaoApi.log("bind device ,your will bind device ,id is " + gizWifiDevice.getDid());
                } else {
                    //已经绑定的设备，则执行为该设备设置监听,并订阅该设备
                    //如果设备处于离线或者不可用的状态，则不执行订阅监控的操作
                    if (gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline || gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceUnavailable) {
                        Toast.makeText(SmartCabinetDeviceListActivity.this, "设备处于" + gizWifiDevice.getNetStatus() + "状态", Toast.LENGTH_LONG).show();
                        SmartSicaoApi.log("the device is not control ,that net status is " + gizWifiDevice.getNetStatus());
                    } else {
                        xCabinetApi.bindDevice(gizWifiDevice, mBindListener);
                        XUserData.setCurrentCabinetId(SmartCabinetDeviceListActivity.this, gizWifiDevice.getDid());
                        SmartSicaoApi.log("set current device  ,your set current device id is " + gizWifiDevice.getDid());
                    }
                }
            }
        });
    }

    @Override
    public void message(Message msg) {
        int what = msg.what;
        if (what == XConfig.BASE_UPDATE_ACTION) {
            initDate(xCabinetApi.getCacheDeviceList());
        }
    }

    @Override
    public void requestPermissionError() {
        super.requestPermissionError();
        Toast("相机授权异常,请重试!");
        finish();
    }

    @Override
    public void requestPermissionSuccess(int requestCode) {
        super.requestPermissionSuccess(requestCode);
        if (requestCode == 10086) {
            startActivity(new Intent(SmartCabinetDeviceListActivity.this, ActivityCapture.class));
        } else {
            Toast("授权异常,请重试!");
            finish();
        }
    }

    /***
     * 解绑设备
     *
     * @param device
     */
    public void bindDevice(final GizWifiDevice device, final boolean bind) {
        final XWarnDialog dialog = new XWarnDialog(SmartCabinetDeviceListActivity.this);
        if (bind) {
            dialog.setTitle("设备绑定");
            dialog.setContent("您将要绑定该设备,绑定成功后您可以对该设备进行控制操作!");
        } else {
            dialog.setTitle("解绑警告");
            dialog.setContent("设备解绑后您将不再拥有该设备,请谨慎操作!");
        }
        dialog.show();
        dialog.setOnListener(new XWarnDialog.OnClickListener() {
            @Override
            public void makeSure() {
                dialog.dismiss();
                showProgress(true);
                if (bind) {
                    //绑定该设备
                    GizWifiSDK.sharedInstance().bindDevice(XUserData.getCabinetUid(SmartCabinetDeviceListActivity.this),
                            XUserData.getCabinetToken(SmartCabinetDeviceListActivity.this),
                            device.getDid(), device.getPasscode(), device.getRemark());
                } else {
                    xCabinetApi.unBindDevice(XUserData.getCabinetUid(SmartCabinetDeviceListActivity.this), XUserData.getCabinetToken(SmartCabinetDeviceListActivity.this),
                            device.getDid());
                }
            }

            @Override
            public void cancle() {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void setSubscribeSuccess(GizWifiDevice device, boolean isSubscribed) {
        super.setSubscribeSuccess(device, isSubscribed);
        /***
         * 订阅OK,需再次判断是否已经是绑定了该设备，若没有绑定,则说明该用户没有绑定权限
         */
        if (isSubscribed) {
            Toast("操作成功!");
            XUserData.setCurrentCabinetId(SmartCabinetDeviceListActivity.this, device.getDid());
            finish();
        } else {
            showProgress(false);
            startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
        }
    }

    @Override
    public void setSubscribeError(GizWifiErrorCode result) {
        super.setSubscribeError(result);
        /***
         * 订阅失败
         */
        showProgress(false);
        Toast(result.toString());
    }

    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        /***
         * 绑定成功，开始订阅该设备
         */
        xCabinetApi.bindDevice(gizWifiDevice, mBindListener);
    }

    @Override
    public void bindError(GizWifiErrorCode result) {
        super.bindError(result);
        /***
         * 绑定失败
         */
        showProgress(false);
        Toast(result.toString());
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
    }

    @Override
    public void unbindSuccess(String did) {
        super.unbindSuccess(did);
        /***
         * 解绑成功
         */
        showProgress(false);
        //如果解绑的是当前正在控制的设备，则更新当前设备的标记
        if (did.equals(XUserData.getCurrentCabinetId(this))) {
            XUserData.setCurrentCabinetId(this, "");
        }
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "3"));
        finish();
    }

    @Override
    public void unbindError(GizWifiErrorCode result) {
        super.unbindError(result);
        /***
         * 解绑失败
         */
        showProgress(false);
        Toast(result.toString());
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "4"));
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
