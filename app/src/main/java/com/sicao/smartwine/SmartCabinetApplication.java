package com.sicao.smartwine;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.sharesdk.framework.ShareSDK;

public class SmartCabinetApplication extends Application {
    /**
     * 接口使用的线程池
     */
    public static ExecutorService mThreadPool = Executors
            .newCachedThreadPool();
    /**
     * 窗口管理
     */
    private WindowManager mManager = null;
    public static DisplayMetrics metrics = null;
    /**
     * 退出应用使用
     */
    public static ArrayList<AppCompatActivity> activities=new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化屏幕参数
        mManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        mManager.getDefaultDisplay().getMetrics(metrics);
        //初始化图片处理器
        Fresco.initialize(this);
    }


}
