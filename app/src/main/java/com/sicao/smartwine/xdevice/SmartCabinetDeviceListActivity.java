package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetDeviceAdapter;
import com.sicao.smartwine.xwidget.dialog.SmartCabinetSettingDialog;
import com.sicao.smartwine.xwidget.dialog.XWarnDialog;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenu;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuCreator;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuItem;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] menu = new String[]{"扫码添加设备", "配置新设备"};
        smartCabinetSettingDialog = new SmartCabinetSettingDialog(this);
        smartCabinetSettingDialog.update(menu);
        smartCabinetSettingDialog.setMenuItemClickListener(new SmartCabinetSettingDialog.MenuItemClickListener() {
            @Override
            public void onClick(int position, String value) {
                smartCabinetSettingDialog.dismiss();
                if (position == 0) {
                    //扫码添加设备
                    startActivity(new Intent(SmartCabinetDeviceListActivity.this, ActivityCapture.class));
                } else {
                    //配置新设备
                    startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetConfigActivity.class));
                }
                finish();
            }
        });
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
        super.refushDeviceList(deviceList);
        initDate(deviceList);
    }

    @Override
    public void update(boolean update, String action) {
        super.update(update, action);
        //刷新设备列表
        initDate(xCabinetApi.getCacheDeviceList());
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_list;
    }

    public void init() {
        mDeviceListView = (SwipeMenuListView) findViewById(R.id.view4);
        mAdapter = new SmartCabinetDeviceAdapter(this, mListData);
        mDeviceListView.setAdapter(mAdapter);
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("添加");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartCabinetSettingDialog.showAsDropDown(v, SmartCabinetApplication.metrics.widthPixels / 2 - smartCabinetSettingDialog.getWidth() / 2, 0);
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
                GizWifiDevice device = mListData.get(position);
                if (device.isBind()) {
                    //已经绑定的设备才可以处理解绑动作
                    bindDevice(device, false);
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
                GizWifiDevice device = mListData.get(position);
                if (!device.isBind()) {
                    //没有绑定的设备，执行绑定设备，并为其设置监听并订阅
                    bindDevice(device, true);
                    XUserData.setCurrentCabinetId(SmartCabinetDeviceListActivity.this, device.getDid());
                    SmartSicaoApi.log("bind device ,your will bind device ,id is " + device.getDid());
                } else {
                    //已经绑定的设备，则执行为该设备设置监听,并订阅该设备
                    xCabinetApi.bindDevice(device, mBindListener);
                    XUserData.setCurrentCabinetId(SmartCabinetDeviceListActivity.this, device.getDid());
                    SmartSicaoApi.log("set current device  ,your set current device id is " + device.getDid());
                }
            }
        });
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
                    xCabinetApi.bindDevice(device, mBindListener);
                } else {
                    xCabinetApi.unBindDevice(XUserData.getCabinetUid(SmartCabinetDeviceListActivity.this), XUserData.getCabinetToken(SmartCabinetDeviceListActivity.this), device.getDid());
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
         * 订阅OK
         */
        Toast.makeText(this, "操作成功!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void setSubscribeError(GizWifiErrorCode result) {
        super.setSubscribeError(result);
        /***
         * 订阅失败
         */
        showProgress(false);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        /***
         * 绑定成功，
         */
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
    }

    @Override
    public void bindError(GizWifiErrorCode result) {
        super.bindError(result);
        /***
         * 绑定失败
         */
        showProgress(false);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
    }

    @Override
    public void unbindSuccess() {
        super.unbindSuccess();
        /***
         * 解绑成功
         */
        showProgress(false);
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "3"));
    }

    @Override
    public void unbindError(GizWifiErrorCode result) {
        super.unbindError(result);
        /***
         * 解绑失败
         */
        showProgress(false);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(SmartCabinetDeviceListActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "4"));
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
