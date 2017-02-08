package com.sicao.smartwine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XSmartCabinetListener;
import com.sicao.smartwine.xhttp.XSmartCabinetReceiver;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_OPENAPI_USERNAME_UNAVALIABLE;
import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING;

public abstract class SmartCabinetActivity extends Activity implements XSmartCabinetListener{
    //硬件部分API
    protected SmartCabinetApi xCabinetApi;
    protected XDeviceListener mBandListener;
    //非硬件部分API
    protected SmartSicaoApi xSicaoApi;
    //内容布局
    RelativeLayout mContent;
    //进度框
    private View mProgressView;
    //顶部右侧按钮
    protected TextView mRightText;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * 监控几个系统广播,用于更新页面设备信息
     */
    private XSmartCabinetReceiver xUpdateSmartCabinetReceiver;

    /**
     * 设置填充的布局ID
     *
     * @return
     */
    protected abstract int setView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        xSicaoApi = new SmartSicaoApi();
        mBandListener = new XDeviceListener();
        xCabinetApi = new SmartCabinetApi();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mContent = (RelativeLayout) findViewById(R.id.base_content_layout);
        mRightText= (TextView) findViewById(R.id.base_top_right_icon);
        mProgressView = findViewById(R.id.login_progress);
        mContent.addView(View.inflate(this, setView(), null));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //每次启动activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(new GizWifiSDKListener() {
            @Override
            public void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
                //用户注册回调
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 注册成功
                    XUserData.saveCabinetUid(SmartCabinetActivity.this, uid);
                    XUserData.saveCabinetToken(SmartCabinetActivity.this, token);
                    registerSuccess();
                } else if (result == GIZ_OPENAPI_USERNAME_UNAVALIABLE) {
                    //用户名不可用----此处处理为已经注册过该用户名应转为登录
                    registerSuccess();
                } else {
                    // 注册失败
                    registerError(result);
                }
            }

            @Override
            public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
                super.didUserLogin(result, uid, token);
                //用户登录回调
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 登录成功
                    XUserData.setUID(SmartCabinetActivity.this, uid);
                    XUserData.saveToken(SmartCabinetActivity.this, token);
                    loginSuccess();
                } else {
                    // 登录失败
                    loginError(result);
                }
            }

            @Override
            public void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
                //获取验证码
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 请求成功
                    requestCodeSuccess();
                } else {
                    // 请求失败
                    requestCodeError(result);
                }
            }

            @Override
            public void didChangeUserPassword(GizWifiErrorCode result) {
                //重置密码，修改密码
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 修改成功
                    changePasswordSuccess();
                } else {
                    // 修改失败
                    changePasswordError(result);
                }
            }

            @Override
            public void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
                //等待配置完成或超时，回调配置完成接口
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 配置成功
                    startConfig(mac, did, productKey);
                } else if (result == GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) {
                    // 正在配置
                    configing(mac, did, productKey);
                } else {
                    // 配置失败
                    configError(result);
                }
            }

            @Override
            public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
                // 提示错误原因
                if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {

                }
                // 显示变化后的设备列表

            }

            @Override
            public void didUnbindDevice(GizWifiErrorCode result, String did) {
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 解绑成功
                    unbindSuccess();
                } else {
                    // 解绑失败
                    unbindError(result);
                }
            }

            @Override
            public void didBindDevice(GizWifiErrorCode result, String did) {
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 非局域网设备绑定成功
                    bindSuccess(did);
                } else {
                    // 非局域网设备绑定失败
                    bindError(result);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //注册监听广播
        xUpdateSmartCabinetReceiver=new XSmartCabinetReceiver();
        xUpdateSmartCabinetReceiver.setSmartCabinetListener(this);
        IntentFilter filter=new IntentFilter();
        // 开机广播
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        // 网络状态发生变化
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // 屏幕打开
        filter.addAction(Intent.ACTION_SCREEN_ON);
        //时间广播
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(xUpdateSmartCabinetReceiver,filter);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(xUpdateSmartCabinetReceiver);
    }

    @Override
    public void update(boolean update, String action) {

    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    /**
     * 获取appSecret
     *
     * @return appSecret
     */
    public native String appSecretFromJNI();


    class XDeviceListener extends GizWifiDeviceListener {
        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 订阅或解除订阅成功
                setSubscribeSuccess(device, isSubscribed);
            } else {
                // 失败
                setSubscribeError(result);
            }
        }

        @Override
        public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo) {
            StringBuilder sb = new StringBuilder();
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                sb.append("Wifi Hardware Version:" + hardwareInfo.get("wifiHardVersion")
                        + "\r\n");
                sb.append("Wifi Software Version:" + hardwareInfo.get("wifiSoftVersion")
                        + "\r\n");
                sb.append("MCU Hardware Version:" + hardwareInfo.get("mcuHardVersion")
                        + "\r\n");
                sb.append("MCU Software Version:" + hardwareInfo.get("mcuSoftVersion")
                        + "\r\n");
                sb.append("Firmware Id:" + hardwareInfo.get("wifiFirmwareId") + "\r\n");
                sb.append("Firmware Version:" + hardwareInfo.get("wifiFirmwareVer")
                        + "\r\n");
                sb.append("Product Key:" + hardwareInfo.get("productKey") + "\r\n");
                sb.append("Device ID:" + device.getDid() + "\r\n");
                sb.append("Device IP:" + device.getIPAddress() + "\r\n");
                sb.append("Device MAC:" + device.getMacAddress() + "\r\n");
            } else {
                sb.append("获取失败，错误号：" + result);
            }
        }

        @Override
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int action) {
            //接收设备状态结果
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 数据解析与3.5.3相同
                // 已定义的设备数据点，有布尔、数值和枚举型数据
                if (dataMap.get("data") != null) {
                    ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
                    // 普通数据点，打印对应的key和value
                    StringBuilder sb = new StringBuilder();
                    for (String key : map.keySet()) {
                        sb.append(key + "  :" + map.get(key) + "\r\n");
                        Toast.makeText(SmartCabinetActivity.this,
                                sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // 查询失败

            }
        }
    }

    public void registerError(GizWifiErrorCode result) {
    }

    public void registerSuccess() {
    }

    public void loginError(GizWifiErrorCode result) {
    }

    public void loginSuccess() {
    }

    public void requestCodeSuccess() {
    }

    public void requestCodeError(GizWifiErrorCode result) {
    }

    public void changePasswordSuccess() {
    }

    public void changePasswordError(GizWifiErrorCode result) {
    }

    public void startConfig(String mac, String did, String productKey) {
    }

    public void configing(String mac, String did, String productKey) {
    }

    public void configError(GizWifiErrorCode result) {
    }

    public void bindSuccess(String did) {
    }

    public void bindError(GizWifiErrorCode result) {
    }

    public void unbindSuccess() {
    }

    public void unbindError(GizWifiErrorCode result) {
    }

    public void setSubscribeSuccess(GizWifiDevice device, boolean isSubscribed) {
    }

    public void setSubscribeError(GizWifiErrorCode result) {
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContent.setVisibility(show ? View.GONE : View.VISIBLE);
            mContent.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContent.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mContent.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
