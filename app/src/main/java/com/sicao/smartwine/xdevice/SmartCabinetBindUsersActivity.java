package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetUserAdapter;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenu;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuCreator;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuItem;
import com.sicao.smartwine.xwidget.device.swipemenulistview.SwipeMenuListView;
import com.sicao.smartwine.xwidget.dialog.SmartCabinetSettingDialog;
import com.sicao.smartwine.xwidget.dialog.XWarnDialog;
import com.sicao.smartwine.xwidget.refresh.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/***
 * 酒柜绑定的用户列表
 */
public class SmartCabinetBindUsersActivity extends SmartCabinetActivity {
    //用户列表
    SwipeMenuListView mDeviceListView;
    //用户列表数据源
    List<GizUserInfo> mListData = new ArrayList<GizUserInfo>();
    //数据列表适配器
    SmartCabinetUserAdapter mAdapter;
    //设备
    GizWifiDevice mDevice=null;
    //刷新控件
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice= (GizWifiDevice) getIntent().getExtras().get("GizWifiDevice");
        GizDeviceSharing.getBindingUsers(XUserData.getCabinetToken(this),mDevice.getDid());
        init();
    }

    public void initDate(List<GizUserInfo> deviceList) {
        mListData = deviceList;
        mAdapter.update(mListData);
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_list;
    }

    public void init() {
        mDeviceListView = (SwipeMenuListView) findViewById(R.id.view4);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        mAdapter = new SmartCabinetUserAdapter(this, mListData);
        mDeviceListView.setAdapter(mAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.remove);
                menu.addMenuItem(deleteItem);
            }
        };
        mDeviceListView.setMenuCreator(creator);
        mDeviceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                //"设备解绑后您将不再拥有该设备,请谨慎操作!"
                GizUserInfo device = mListData.get(position);
                //解绑该用户
                unBindGuestUser(device);
            }
        });
        //
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(10091,2000);
            }
        });
    }
    @Override
    public void message(Message msg) {
        int what=msg.what;
        if (what==10091){
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(SmartCabinetBindUsersActivity.this, "操作成功!", Toast.LENGTH_SHORT).show();
        }
    }
    /***
     * 解绑用户
     *
     */
    public void unBindGuestUser(final GizUserInfo device) {
        final XWarnDialog dialog = new XWarnDialog(SmartCabinetBindUsersActivity.this);
        dialog.setTitle("解绑警告");
        dialog.setContent("解绑后该用户将不再拥有此设备,请谨慎操作!");
        dialog.show();
        dialog.setOnListener(new XWarnDialog.OnClickListener() {
            @Override
            public void makeSure() {
                dialog.dismiss();
                showProgress(true);
                GizDeviceSharing.unbindUser(XUserData.getCabinetToken(SmartCabinetBindUsersActivity.this),mDevice.getDid(),device.getUid());
            }
            @Override
            public void cancle() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void unBindGuestUserSuccess(String deviceID, String guestUID) {
        super.unBindGuestUserSuccess(deviceID, guestUID);
        Toast.makeText(this, "操作成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void getBindingUsersSuccess(String deviceID, List<GizUserInfo> bindUsers) {
        super.getBindingUsersSuccess(deviceID, bindUsers);
        initDate(bindUsers);
    }

    @Override
    public void getBindingUsersError(String result) {
        super.getBindingUsersError(result);
        Toast.makeText(this, "操作失败,请刷新重试"+result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void unBindGuestUserError(String result) {
        super.unBindGuestUserError(result);
        Toast.makeText(this, "操作失败,请重试"+result, Toast.LENGTH_LONG).show();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
