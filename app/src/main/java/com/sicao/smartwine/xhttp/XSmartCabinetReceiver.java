package com.sicao.smartwine.xhttp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * 接收系统网络和时间的广播，用于被动回调刷新设备信息函数以达到及时更新设备信息
 */
public class XSmartCabinetReceiver extends BroadcastReceiver {

    private XSmartCabinetListener smartCabinetListener;

    public void setSmartCabinetListener(XSmartCabinetListener smartCabinetListener) {
        this.smartCabinetListener = smartCabinetListener;
    }

    public XSmartCabinetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null != smartCabinetListener) {
            smartCabinetListener.update(true, action);
        }
    }
}
