package com.sicao.smartwine;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSharingListener;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xhttp.XSmartCabinetListener;
import com.sicao.smartwine.xhttp.XSmartCabinetReceiver;
import com.sicao.smartwine.xwidget.refresh.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_OPENAPI_USERNAME_UNAVALIABLE;
import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING;

public abstract class SmartCabinetActivity extends AppCompatActivity implements XSmartCabinetListener {
    //硬件部分API
    protected SmartCabinetApi xCabinetApi;
    protected XDeviceListener mBindListener;
    //非硬件部分API
    protected SmartSicaoApi xSicaoApi;
    //内容布局
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RelativeLayout mContent;
    //主页使用的内容布局
    protected  RelativeLayout mContent2;
    //进度框
    private View mProgressView;
    //顶部右侧按钮
    protected TextView mRightText;
    //頂部标题
    protected TextView mCenterTitle;
    //进度框下面的提示语句
    protected TextView mHintText;
    //交互使用Handler
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 777777) {
                swipeRefreshLayout.setRefreshing(false);
            } else if (msg.what == 999999) {
                swipeRefreshLayout.setLoading(false);
            }
            message(msg);
        }
    };

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public void Toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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
        GizWifiSDK.sharedInstance().startWithAppID(getApplicationContext(), getAppID());
//        overridePendingTransition(R.anim.activity_out_anim, R.anim.activity_in_anim);// 淡出淡入动画效果
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        xSicaoApi = new SmartSicaoApi();
        mBindListener = new XDeviceListener();
        xCabinetApi = new SmartCabinetApi();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        mContent = (RelativeLayout) findViewById(R.id.base_content_layout);
        mContent2= (RelativeLayout) findViewById(R.id.base_content_layout2);
        mRightText = (TextView) findViewById(R.id.base_top_right_icon);
        mProgressView = findViewById(R.id.login_progress);
        mCenterTitle = (TextView) findViewById(R.id.base_top_center_text);
        mHintText = (TextView) findViewById(R.id.hint_text);
        //兼容首页单独动画加载刷新数据
        if (this.getClass().getSimpleName().contains("SmartCabinetDeviceInfoActivity")){
            mContent2.addView(View.inflate(this, setView(), null));
            mContent2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
            mContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
        }else{
            mContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
            mContent2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            mContent.addView(View.inflate(this, setView(), null));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(777777, 2000);
            }
        });
        swipeRefreshLayout.setOnLoadListener(new SwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                handler.sendEmptyMessageDelayed(999999, 2000);
            }
        });
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
        GizDeviceSharing.setListener(mSharingListener);
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

    /***
     * 判断是否审核通过了某一个权限
     *
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /***
     * 申请权限部分
     *
     * @param permission  请求的具体权限
     * @param requestCode 请求码
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission(String permission, int requestCode) {
        mPermissonRequestCode = requestCode;
        requestPermissions(new String[]{permission}, requestCode);
    }

    int mPermissonRequestCode = 0;

    /***
     * 权限请求失败
     */
    public void requestPermissionError() {
    }

    /***
     * 权限请求OK
     */
    public void requestPermissionSuccess(int requestCode) {
    }

    /***
     * 申请权限返回结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == mPermissonRequestCode) {
            if (permissions.length != 1 || grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermissionError();
            } else {
                requestPermissionSuccess(requestCode);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /***
     * @param update 是否需要更新设备信息
     * @param action 动作类型
     */
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


    public native String getAppID();

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

            swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            swipeRefreshLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            SmartSicaoApi.log("Version=" + GizWifiSDK.sharedInstance().getVersion());
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
                configSuccess(mac, did, productKey);
            } else if (result == GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) {
                // 正在配置
                configing(mac, did, productKey);
            } else {
                // 配置失败
                configError(result);
            }
            xSicaoApi.log("mac=" + mac + ",did=" + did + ",key=" + productKey);
        }

        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            // 提示错误原因
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                SmartSicaoApi.log("result: " + result.name() + "---" + errorCodeToString(result));
                return;
            }
            if (deviceList.size() == 0) {
                SmartSicaoApi.log("result: 设备列表为空 ");
                return;
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

    /***
     * 设备分享监听
     */
    private GizDeviceSharingListener mSharingListener = new GizDeviceSharingListener() {
        // 实现设备分享的回调
        @Override
        public void didSharingDevice(GizWifiErrorCode result, String deviceID, int sharingID, Bitmap QRCodeImage) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 分享成功
                getSharingInfoSuccess(deviceID, sharingID, QRCodeImage);
            } else {
                // 分享失败
                getSharingInfoError(errorCodeToString(result));
            }
        }

        //获取设备的绑定用户
        @Override
        public void didGetBindingUsers(GizWifiErrorCode result, String deviceID, List<GizUserInfo> bindUsers) {
            super.didGetBindingUsers(result, deviceID, bindUsers);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                getBindingUsersSuccess(deviceID, bindUsers);
            } else {
                getBindingUsersError(errorCodeToString(result));
            }
        }

        //解绑绑定设备的子账户
        @Override
        public void didUnbindUser(GizWifiErrorCode result, String deviceID, String guestUID) {
            super.didUnbindUser(result, deviceID, guestUID);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                unBindGuestUserSuccess(deviceID, guestUID);
            } else {
                unBindGuestUserError(errorCodeToString(result));
            }
        }
    };

    public void message(Message msg) {
    }

    public void unBindGuestUserSuccess(String deviceID, String guestUID) {
    }

    public void unBindGuestUserError(String result) {
    }

    public void getBindingUsersSuccess(String deviceID, List<GizUserInfo> bindUsers) {
    }

    public void getBindingUsersError(String result) {
    }

    public void getSharingInfoError(String result) {
    }

    public void getSharingInfoSuccess(String deviceID, int sharingID, Bitmap QRCodeImage) {
    }


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

    public void configSuccess(String mac, String did, String productKey) {
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

    public String errorCodeToString(GizWifiErrorCode errorCode) {
        String errorString = (String) getText(R.string.UNKNOWN_ERROR);
        switch (errorCode) {
            case GIZ_SDK_PARAM_FORM_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_PARAM_FORM_INVALID);
                break;
            case GIZ_SDK_CLIENT_NOT_AUTHEN:
                errorString = (String) getText(R.string.GIZ_SDK_CLIENT_NOT_AUTHEN);
                break;
            case GIZ_SDK_CLIENT_VERSION_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_CLIENT_VERSION_INVALID);
                break;
            case GIZ_SDK_UDP_PORT_BIND_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_UDP_PORT_BIND_FAILED);
                break;
            case GIZ_SDK_DAEMON_EXCEPTION:
                errorString = (String) getText(R.string.GIZ_SDK_DAEMON_EXCEPTION);
                break;
            case GIZ_SDK_PARAM_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_PARAM_INVALID);
                break;
            case GIZ_SDK_APPID_LENGTH_ERROR:
                errorString = (String) getText(R.string.GIZ_SDK_APPID_LENGTH_ERROR);
                break;
            case GIZ_SDK_LOG_PATH_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_LOG_PATH_INVALID);
                break;
            case GIZ_SDK_LOG_LEVEL_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_LOG_LEVEL_INVALID);
                break;
            case GIZ_SDK_DEVICE_CONFIG_SEND_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONFIG_SEND_FAILED);
                break;
            case GIZ_SDK_DEVICE_CONFIG_IS_RUNNING:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING);
                break;
            case GIZ_SDK_DEVICE_CONFIG_TIMEOUT:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONFIG_TIMEOUT);
                break;
            case GIZ_SDK_DEVICE_DID_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_DID_INVALID);
                break;
            case GIZ_SDK_DEVICE_MAC_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_MAC_INVALID);
                break;
            case GIZ_SDK_SUBDEVICE_DID_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_SUBDEVICE_DID_INVALID);
                break;
            case GIZ_SDK_DEVICE_PASSCODE_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_PASSCODE_INVALID);
                break;
            case GIZ_SDK_DEVICE_NOT_SUBSCRIBED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_NOT_SUBSCRIBED);
                break;
            case GIZ_SDK_DEVICE_NO_RESPONSE:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_NO_RESPONSE);
                break;
            case GIZ_SDK_DEVICE_NOT_READY:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_NOT_READY);
                break;
            case GIZ_SDK_DEVICE_NOT_BINDED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_NOT_BINDED);
                break;
            case GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND);
                break;
            case GIZ_SDK_DEVICE_GET_STATUS_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_GET_STATUS_FAILED);
                break;
            case GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR);
                break;
            case GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE);
                break;
            case GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND);
                break;
            case GIZ_SDK_BIND_DEVICE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_BIND_DEVICE_FAILED);
                break;
            case GIZ_SDK_UNBIND_DEVICE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_UNBIND_DEVICE_FAILED);
                break;
            case GIZ_SDK_DNS_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_DNS_FAILED);
                break;
            case GIZ_SDK_M2M_CONNECTION_SUCCESS:
                errorString = (String) getText(R.string.GIZ_SDK_M2M_CONNECTION_SUCCESS);
                break;
            case GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED);
                break;
            case GIZ_SDK_CONNECTION_TIMEOUT:
                errorString = (String) getText(R.string.GIZ_SDK_CONNECTION_TIMEOUT);
                break;
            case GIZ_SDK_CONNECTION_REFUSED:
                errorString = (String) getText(R.string.GIZ_SDK_CONNECTION_REFUSED);
                break;
            case GIZ_SDK_CONNECTION_ERROR:
                errorString = (String) getText(R.string.GIZ_SDK_CONNECTION_ERROR);
                break;
            case GIZ_SDK_CONNECTION_CLOSED:
                errorString = (String) getText(R.string.GIZ_SDK_CONNECTION_CLOSED);
                break;
            case GIZ_SDK_SSL_HANDSHAKE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_SSL_HANDSHAKE_FAILED);
                break;
            case GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED);
                break;
            case GIZ_SDK_INTERNET_NOT_REACHABLE:
                errorString = (String) getText(R.string.GIZ_SDK_INTERNET_NOT_REACHABLE);
                break;
            case GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR:
                errorString = (String) getText(R.string.GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR);
                break;
            case GIZ_SDK_HTTP_ANSWER_PARAM_ERROR:
                errorString = (String) getText(R.string.GIZ_SDK_HTTP_ANSWER_PARAM_ERROR);
                break;
            case GIZ_SDK_HTTP_SERVER_NO_ANSWER:
                errorString = (String) getText(R.string.GIZ_SDK_HTTP_SERVER_NO_ANSWER);
                break;
            case GIZ_SDK_HTTP_REQUEST_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_HTTP_REQUEST_FAILED);
                break;
            case GIZ_SDK_OTHERWISE:
                errorString = (String) getText(R.string.GIZ_SDK_OTHERWISE);
                break;
            case GIZ_SDK_MEMORY_MALLOC_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_MEMORY_MALLOC_FAILED);
                break;
            case GIZ_SDK_THREAD_CREATE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_THREAD_CREATE_FAILED);
                break;
            case GIZ_SDK_USER_ID_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_USER_ID_INVALID);
                break;
            case GIZ_SDK_TOKEN_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_TOKEN_INVALID);
                break;
            case GIZ_SDK_GROUP_ID_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_GROUP_ID_INVALID);
                break;
            case GIZ_SDK_GROUPNAME_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_GROUPNAME_INVALID);
                break;
            case GIZ_SDK_GROUP_PRODUCTKEY_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_GROUP_PRODUCTKEY_INVALID);
                break;
            case GIZ_SDK_GROUP_FAILED_DELETE_DEVICE:
                errorString = (String) getText(R.string.GIZ_SDK_GROUP_FAILED_DELETE_DEVICE);
                break;
            case GIZ_SDK_GROUP_FAILED_ADD_DEVICE:
                errorString = (String) getText(R.string.GIZ_SDK_GROUP_FAILED_ADD_DEVICE);
                break;
            case GIZ_SDK_GROUP_GET_DEVICE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_GROUP_GET_DEVICE_FAILED);
                break;
            case GIZ_SDK_DATAPOINT_NOT_DOWNLOAD:
                errorString = (String) getText(R.string.GIZ_SDK_DATAPOINT_NOT_DOWNLOAD);
                break;
            case GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE:
                errorString = (String) getText(R.string.GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE);
                break;
            case GIZ_SDK_DATAPOINT_PARSE_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_DATAPOINT_PARSE_FAILED);
                break;
            // case GIZ_SDK_NOT_INITIALIZED:
            // errorString= (String) getText(R.string.GIZ_SDK_SDK_NOT_INITIALIZED);
            // break;
            case GIZ_SDK_APK_CONTEXT_IS_NULL:
                errorString = (String) getText(R.string.GIZ_SDK_APK_CONTEXT_IS_NULL);
                break;
            case GIZ_SDK_APK_PERMISSION_NOT_SET:
                errorString = (String) getText(R.string.GIZ_SDK_APK_PERMISSION_NOT_SET);
                break;
            case GIZ_SDK_CHMOD_DAEMON_REFUSED:
                errorString = (String) getText(R.string.GIZ_SDK_CHMOD_DAEMON_REFUSED);
                break;
            case GIZ_SDK_EXEC_DAEMON_FAILED:
                errorString = (String) getText(R.string.GIZ_SDK_EXEC_DAEMON_FAILED);
                break;
            case GIZ_SDK_EXEC_CATCH_EXCEPTION:
                errorString = (String) getText(R.string.GIZ_SDK_EXEC_CATCH_EXCEPTION);
                break;
            case GIZ_SDK_APPID_IS_EMPTY:
                errorString = (String) getText(R.string.GIZ_SDK_APPID_IS_EMPTY);
                break;
            case GIZ_SDK_UNSUPPORTED_API:
                errorString = (String) getText(R.string.GIZ_SDK_UNSUPPORTED_API);
                break;
            case GIZ_SDK_REQUEST_TIMEOUT:
                errorString = (String) getText(R.string.GIZ_SDK_REQUEST_TIMEOUT);
                break;
            case GIZ_SDK_DAEMON_VERSION_INVALID:
                errorString = (String) getText(R.string.GIZ_SDK_DAEMON_VERSION_INVALID);
                break;
            case GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID:
                errorString = (String) getText(R.string.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID);
                break;
            case GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED:
                errorString = (String) getText(R.string.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED);
                break;
            case GIZ_SDK_NOT_IN_SOFTAPMODE:
                errorString = (String) getText(R.string.GIZ_SDK_NOT_IN_SOFTAPMODE);
                break;
            // case GIZ_SDK_PHONE_WIFI_IS_UNAVAILABLE:
            // errorString= (String)
            // getText(R.string.GIZ_SDK_PHONE_WIFI_IS_UNAVAILABLE);
            // break;
            case GIZ_SDK_RAW_DATA_TRANSMIT:
                errorString = (String) getText(R.string.GIZ_SDK_RAW_DATA_TRANSMIT);
                break;
            case GIZ_SDK_PRODUCT_IS_DOWNLOADING:
                errorString = (String) getText(R.string.GIZ_SDK_PRODUCT_IS_DOWNLOADING);
                break;
            case GIZ_SDK_START_SUCCESS:
                errorString = (String) getText(R.string.GIZ_SDK_START_SUCCESS);
                break;
            case GIZ_SITE_PRODUCTKEY_INVALID:
                errorString = (String) getText(R.string.GIZ_SITE_PRODUCTKEY_INVALID);
                break;
            case GIZ_SITE_DATAPOINTS_NOT_DEFINED:
                errorString = (String) getText(R.string.GIZ_SITE_DATAPOINTS_NOT_DEFINED);
                break;
            case GIZ_SITE_DATAPOINTS_NOT_MALFORME:
                errorString = (String) getText(R.string.GIZ_SITE_DATAPOINTS_NOT_MALFORME);
                break;
            case GIZ_OPENAPI_MAC_ALREADY_REGISTERED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_MAC_ALREADY_REGISTERED);
                break;
            case GIZ_OPENAPI_PRODUCT_KEY_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_PRODUCT_KEY_INVALID);
                break;
            case GIZ_OPENAPI_APPID_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_APPID_INVALID);
                break;
            case GIZ_OPENAPI_TOKEN_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_TOKEN_INVALID);
                break;
            case GIZ_OPENAPI_USER_NOT_EXIST:
                errorString = (String) getText(R.string.GIZ_OPENAPI_USER_NOT_EXIST);
                break;
            case GIZ_OPENAPI_TOKEN_EXPIRED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_TOKEN_EXPIRED);
                break;
            case GIZ_OPENAPI_M2M_ID_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_M2M_ID_INVALID);
                break;
            case GIZ_OPENAPI_SERVER_ERROR:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SERVER_ERROR);
                break;
            case GIZ_OPENAPI_CODE_EXPIRED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_CODE_EXPIRED);
                break;
            case GIZ_OPENAPI_CODE_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_CODE_INVALID);
                break;
            case GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED);
                break;
            case GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED);
                break;
            case GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE);
                break;
            case GIZ_OPENAPI_DEVICE_NOT_FOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DEVICE_NOT_FOUND);
                break;
            case GIZ_OPENAPI_FORM_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_FORM_INVALID);
                break;
            case GIZ_OPENAPI_DID_PASSCODE_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DID_PASSCODE_INVALID);
                break;
            case GIZ_OPENAPI_DEVICE_NOT_BOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DEVICE_NOT_BOUND);
                break;
            case GIZ_OPENAPI_PHONE_UNAVALIABLE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_PHONE_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_USERNAME_UNAVALIABLE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_USERNAME_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_USERNAME_PASSWORD_ERROR:
                errorString = (String) getText(R.string.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR);
                break;
            case GIZ_OPENAPI_SEND_COMMAND_FAILED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SEND_COMMAND_FAILED);
                break;
            case GIZ_OPENAPI_EMAIL_UNAVALIABLE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_EMAIL_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_DEVICE_DISABLED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DEVICE_DISABLED);
                break;
            case GIZ_OPENAPI_FAILED_NOTIFY_M2M:
                errorString = (String) getText(R.string.GIZ_OPENAPI_FAILED_NOTIFY_M2M);
                break;
            case GIZ_OPENAPI_ATTR_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_ATTR_INVALID);
                break;
            case GIZ_OPENAPI_USER_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_USER_INVALID);
                break;
            case GIZ_OPENAPI_FIRMWARE_NOT_FOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_FIRMWARE_NOT_FOUND);
                break;
            case GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND);
                break;
            case GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND);
                break;
            case GIZ_OPENAPI_SCHEDULER_NOT_FOUND:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SCHEDULER_NOT_FOUND);
                break;
            case GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID);
                break;
            case GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE);
                break;
            case GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED);
                break;
            case GIZ_OPENAPI_BT_FIRMWARE_NOTHING_TO_UPGRADE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR);
                break;
            case GIZ_OPENAPI_SAVE_KAIROSDB_ERROR:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR);
                break;
            case GIZ_OPENAPI_EVENT_NOT_DEFINED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_EVENT_NOT_DEFINED);
                break;
            case GIZ_OPENAPI_SEND_SMS_FAILED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SEND_SMS_FAILED);
                break;
            // case GIZ_OPENAPI_APPLICATION_AUTH_INVALID:
            // errorString= (String)
            // getText(R.string.GIZ_OPENAPI_APPLICATION_AUTH_INVALID);
            // break;
            case GIZ_OPENAPI_NOT_ALLOWED_CALL_API:
                errorString = (String) getText(R.string.GIZ_OPENAPI_NOT_ALLOWED_CALL_API);
                break;
            case GIZ_OPENAPI_BAD_QRCODE_CONTENT:
                errorString = (String) getText(R.string.GIZ_OPENAPI_BAD_QRCODE_CONTENT);
                break;
            case GIZ_OPENAPI_REQUEST_THROTTLED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_REQUEST_THROTTLED);
                break;
            case GIZ_OPENAPI_DEVICE_OFFLINE:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DEVICE_OFFLINE);
                break;
            case GIZ_OPENAPI_TIMESTAMP_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_TIMESTAMP_INVALID);
                break;
            case GIZ_OPENAPI_SIGNATURE_INVALID:
                errorString = (String) getText(R.string.GIZ_OPENAPI_SIGNATURE_INVALID);
                break;
            case GIZ_OPENAPI_DEPRECATED_API:
                errorString = (String) getText(R.string.GIZ_OPENAPI_DEPRECATED_API);
                break;
            case GIZ_OPENAPI_RESERVED:
                errorString = (String) getText(R.string.GIZ_OPENAPI_RESERVED);
                break;
            case GIZ_PUSHAPI_BODY_JSON_INVALID:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_BODY_JSON_INVALID);
                break;
            case GIZ_PUSHAPI_DATA_NOT_EXIST:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_DATA_NOT_EXIST);
                break;
            case GIZ_PUSHAPI_NO_CLIENT_CONFIG:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_NO_CLIENT_CONFIG);
                break;
            case GIZ_PUSHAPI_NO_SERVER_DATA:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_NO_SERVER_DATA);
                break;
            case GIZ_PUSHAPI_GIZWITS_APPID_EXIST:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_GIZWITS_APPID_EXIST);
                break;
            case GIZ_PUSHAPI_PARAM_ERROR:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_AUTH_KEY_INVALID:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_AUTH_KEY_INVALID);
                break;
            case GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR);
                break;
            case GIZ_PUSHAPI_TYPE_PARAM_ERROR:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_TYPE_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_ID_PARAM_ERROR:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_ID_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID);
                break;
            case GIZ_PUSHAPI_CHANNELID_ERROR_INVALID:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_CHANNELID_ERROR_INVALID);
                break;
            case GIZ_PUSHAPI_PUSH_ERROR:
                errorString = (String) getText(R.string.GIZ_PUSHAPI_PUSH_ERROR);
                break;
            default:
                errorString = (String) getText(R.string.UNKNOWN_ERROR);
                break;
        }
        return errorString;
    }

}
