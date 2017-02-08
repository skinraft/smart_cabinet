package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.SmartCabinet;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetDeviceAdapter;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenu;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuCreator;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuItem;
import com.sicao.smartwine.xwidget.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

/***
 * 我的酒柜列表数据
 *
 * @author techssd
 * @version 1.0.0
 */
public class SmartCabinetDeviceListActivity extends SmartCabinetActivity {
    //设备列表
    SwipeMenuListView mDeviceListView;
    //设备列表数据源
    ArrayList<SmartCabinet> mListData = new ArrayList<SmartCabinet>();
    //数据列表适配器
    SmartCabinetDeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
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
                //扫码
//                  startActivity(new Intent(SmartCabinetDeviceListActivity.this, ActivityCapture.class));
                finish();
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
            }
        });
        //Item的点击事件
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmartCabinet device = mListData.get(position);
                XUserData.setCurrentCabinetId(SmartCabinetDeviceListActivity.this, device.getId());
                SmartSicaoApi.log("switch device ,your selected device id is " + device.getId());
                finish();
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
