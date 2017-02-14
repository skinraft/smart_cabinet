package com.sicao.smartwine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xhttp.XSmartCabinetListener;
import com.sicao.smartwine.xhttp.XSmartCabinetReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_OPENAPI_USERNAME_UNAVALIABLE;
import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING;

public abstract class SmartCabinetActivity extends Activity implements XSmartCabinetListener {
    //硬件部分API
    protected SmartCabinetApi xCabinetApi;
    protected XDeviceListener mBindListener;
    //非硬件部分API
    protected SmartSicaoApi xSicaoApi;
    //内容布局
    protected RelativeLayout mContent;
    //进度框
    private View mProgressView;
    //顶部右侧按钮
    protected TextView mRightText;
    //頂部标题
    protected TextView mCenterTitle;

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
        mBindListener = new XDeviceListener();
        xCabinetApi = new SmartCabinetApi();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mContent = (RelativeLayout) findViewById(R.id.base_content_layout);
        mRightText = (TextView) findViewById(R.id.base_top_right_icon);
        mProgressView = findViewById(R.id.login_progress);
        mCenterTitle = (TextView) findViewById(R.id.base_top_center_text);
        mContent.addView(View.inflate(this, setView(), null));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //注册监听广播
        xUpdateSmartCabinetReceiver = new XSmartCabinetReceiver();
        xUpdateSmartCabinetReceiver.setSmartCabinetListener(this);
        IntentFilter filter = new IntentFilter();
        // 开机广播
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        // 网络状态发生变化
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 屏幕打开
        filter.addAction(Intent.ACTION_SCREEN_ON);
        //时间广播
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(xUpdateSmartCabinetReceiver, filter);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次启动activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(mGizListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(xUpdateSmartCabinetReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void update(boolean update, String action) {
    }

    /**
     * getProductKey
     */
    public native String getProductKey();

    /**
     * 获取appSecret
     */
    public native String getAppSecret();

    /**
     * 获取productSecert
     */
    public native String getProductSecret();

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
                if (action == XConfig.CONFIG_CABINET_MODEL_TEMP_ACTION || action == XConfig.CONFIG_CABINET_SET_LIGHT_ACTION
                        || action == XConfig.CONFIG_CABINET_SET_TEMP_ACTION || action == XConfig.CONFIG_CABINET_WORK_MODEL_ACTION) {
                    //设备属性修改结果回调
                    setCustomInfoSuccess(device);
                    return;
                }
                // 已定义的设备数据点，有布尔、数值和枚举型数据
                if (dataMap.get("data") != null) {
                    ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
                    // 普通数据点，打印对应的key和value
                    JSONObject jsonObject = new JSONObject();
                    for (String key : map.keySet()) {
                        try {
                            jsonObject.put(key, map.get(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //更新设备信息
                    SmartSicaoApi.log("the device info will update " + device.getDid());
                    refushDeviceInfo(device, jsonObject);
                }
            } else {
                showProgress(false);
                SmartSicaoApi.log(result.toString());
            }
        }

        @Override
        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 修改成功
                setCustomInfoSuccess(device);
            } else {
                // 修改失败
                setCustomInfoError(result.toString());
            }
        }
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

    private GizWifiSDKListener mGizListener = new GizWifiSDKListener() {
        @Override
        public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
            if (eventType == GizEventType.GizEventSDK) {
                // SDK的事件通知
                SmartSicaoApi.log("SDK event happened: " + eventID + ", " + eventMessage);
            } else if (eventType == GizEventType.GizEventDevice) {
                // 设备连接断开时可能产生的通知
                GizWifiDevice mDevice = (GizWifiDevice) eventSource;
                SmartSicaoApi.log("device mac: " + mDevice.getMacAddress() + " disconnect caused by eventID: " + eventID + ", eventMessage: " + eventMessage);
            } else if (eventType == GizEventType.GizEventM2MService) {
                // M2M服务返回的异常通知
                SmartSicaoApi.log("M2M domain " + eventSource + " exception happened, eventID: " + eventID + ", eventMessage: " + eventMessage);
            } else if (eventType == GizEventType.GizEventToken) {
                // token失效通知
                SmartSicaoApi.log("token " + eventSource + " expired: " + eventMessage);
                //需要重新登录
                if (!"".equals(XUserData.getCabinetUid(SmartCabinetActivity.this))) {
                    xCabinetApi.login("sicao-" + XUserData.getUID(SmartCabinetActivity.this), XUserData.getPassword(SmartCabinetActivity.this));
                    return;
                }
            }
        }

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
                XUserData.saveCabinetUid(SmartCabinetActivity.this, uid);
                XUserData.saveCabinetToken(SmartCabinetActivity.this, token);
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
        public void didBindDevice(int error, String errorMessage, String did) {
            super.didBindDevice(error, errorMessage, did);
            if (error != 0) {
                Toast.makeText(SmartCabinetActivity.this, errorMessage,
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                bindSuccess(did);
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
                SmartSicaoApi.log("result: " + result.name());
                return;
            }
            if (deviceList.size() == 0) {
                SmartSicaoApi.log("result: 设备列表为空 ");
                return;
            }
            for (GizWifiDevice device : deviceList) {
                //另外保存局域网的设备
                if (device.isLAN()&&!SmartCabinetApplication.mLAN.containsKey(device.getDid())) {
                    SmartCabinetApplication.mLAN.put(device.getDid(),device);
                }
            }
            // 显示变化后的设备列表
            SmartSicaoApi.log("discovered deviceList: " + deviceList);
            refushDeviceList(deviceList);
        }

        @Override
        public void didUnbindDevice(GizWifiErrorCode result, String did) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                unbindSuccess(did);
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
    };

    public void setCustomInfoSuccess(GizWifiDevice device) {
    }

    public void setCustomInfoError(String result) {
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

    public void unbindSuccess(String did) {
    }

    public void unbindError(GizWifiErrorCode result) {
    }

    public void setSubscribeSuccess(GizWifiDevice device, boolean isSubscribed) {
    }

    public void setSubscribeError(GizWifiErrorCode result) {
    }

    public void refushDeviceList(List<GizWifiDevice> deviceList) {
    }

    public void refushDeviceInfo(GizWifiDevice device, JSONObject object) {
    }
}
