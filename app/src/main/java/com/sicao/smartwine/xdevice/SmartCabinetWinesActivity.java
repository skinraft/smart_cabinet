package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesAdpter;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.ArrayList;

public class SmartCabinetWinesActivity extends SmartCabinetActivity implements AdapterView.OnItemClickListener ,View.OnClickListener{
    //当前酒柜
    GizWifiDevice gizWifiDevice;
    //酒款列表数据
    ArrayList<XWineEntity> mWins = new ArrayList<>();
    //酒款列表适配器
    SmartCabinetWinesAdpter smartCabinetWinesAdpter;
    //列表控件
    ListView listView;
    //回到顶部按钮
    FloatingActionButton floatingActionButton;
    int page=1;
    @Override
    protected int setView() {
        return R.layout.activity_smart_cabinet_wines;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init() {
        gizWifiDevice = (GizWifiDevice) getIntent().getExtras().get("cabinet");
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        /////////////////////////
        smartCabinetWinesAdpter=new SmartCabinetWinesAdpter(this,mWins);
        listView.setAdapter(smartCabinetWinesAdpter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast("查看商品详情");
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.fab://回到顶部
                listView.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsList(page);
    }

    @Override
    public void message(Message msg) {
        if (msg.what == XConfig.BASE_UPDATE_ACTION) {
            //刷新
            page=1;
            getGoodsList(page);
        } else if (msg.what == XConfig.BASE_LOAD_ACTION) {
            //加载更多
            page++;
            getGoodsList(page);
        }
    }
    public void getGoodsList(final int page) {
        xSicaoApi.getGoodsByMac(this, gizWifiDevice.getMacAddress(), page, new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                if (page == 1) {
                    mWins = (ArrayList<XWineEntity>) list;
                } else {
                    mWins.addAll((ArrayList<XWineEntity>) list);
                }
                smartCabinetWinesAdpter.upDataAdapter(mWins);
                if (mWins.isEmpty()) {
                    findViewById(R.id.no_wines_hint).setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    findViewById(R.id.no_wines_hint).setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                if (mWins.size()>10){
                    floatingActionButton.setVisibility(View.VISIBLE);
                }else{
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        }, null);
    }
}
