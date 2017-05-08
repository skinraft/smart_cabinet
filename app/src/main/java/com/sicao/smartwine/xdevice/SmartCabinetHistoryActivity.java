package com.sicao.smartwine.xdevice;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdevice.adapter.SmartCabinetWinesHistoryAdpter;
import com.sicao.smartwine.xdevice.entity.XProductHistoryEntity;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.ArrayList;

public class SmartCabinetHistoryActivity extends SmartCabinetActivity {
    String mac = "";
    //历史记录
    ListView list_view;
    //历史记录的适配器
    SmartCabinetWinesHistoryAdpter smartCabinetWinesHistoryAdpter;
    //历史记录的数据
    ArrayList<XProductHistoryEntity> historyEntities = new ArrayList<>();
    int page = 1;

    @Override
    protected int setView() {
        return R.layout.activity_smart_cabinet_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mac = getIntent().getExtras().getString("mac");
        init();
        getHistory(mac, page);
    }

    private void init() {
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
    public void message(Message msg) {
        if (msg.what == XConfig.BASE_UPDATE_ACTION) {
            //刷新
            page = 1;
            getHistory(mac, page);
        } else if (msg.what == XConfig.BASE_LOAD_ACTION) {
            page++;
            getHistory(mac, page);
        }
    }

    /***
     * 获取该设备的历史记录
     * @param mac
     */
    public void getHistory(String mac, final int p) {
        xSicaoApi.getHistoryByMac(this, mac, p + "", new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                if (p != 1) {
                    historyEntities.addAll((ArrayList<XProductHistoryEntity>) list);
                } else {
                    historyEntities = (ArrayList<XProductHistoryEntity>) list;
                }
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
