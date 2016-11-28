package com.sicao.smartwine;

import android.app.Application;
import android.util.Log;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

/**
 * Created by techssd on 2016/11/28.
 */

public class SmartCabinetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }

        GizWifiSDK.sharedInstance().setListener(new GizWifiSDKListener() {
            @Override
            public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
                if (eventType == GizEventType.GizEventSDK) {
                   // SDK的事件通知
                    Log.i("GizWifiSDK", "SDK event happened: " + eventID + ", " + eventMessage);
                } else if (eventType == GizEventType.GizEventDevice) {
                    // 设备连接断开时可能产生的通知
                    GizWifiDevice mDevice = (GizWifiDevice)eventSource;
                    Log.i("GizWifiSDK", "device mac: " + mDevice.getMacAddress() + " disconnect caused by eventID: " + eventID + ", eventMessage: " + eventMessage);
                } else if (eventType == GizEventType.GizEventM2MService) {
                    // M2M服务返回的异常通知
                    Log.i("GizWifiSDK", "M2M domain " + (String)eventSource + " exception happened, eventID: " + eventID + ", eventMessage: " + eventMessage);
                } else if (eventType == GizEventType.GizEventToken) {
                    // token失效通知
                    Log.i("GizWifiSDK", "token " + (String)eventSource + " expired: " + eventMessage);
                }
            }
        });
        GizWifiSDK.sharedInstance().startWithAppID(getApplicationContext(), "cd688469b42446e99a6130c9550c9ee7");
    }
}
