package com.sicao.smartwine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sicao.smartwine.xshare.XUserData;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING;

public class SmartCabinetActivity extends Activity {

    protected SmartCabinetApi xApi;
    protected XDeviceListener mBandListener;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xApi = new SmartCabinetApi();
        mBandListener = new XDeviceListener();
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        //每次启动activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(new GizWifiSDKListener() {
            @Override
            public void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
                //用户注册回调
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 注册成功
                    XUserData.setUID(SmartCabinetActivity.this, uid);
                    XUserData.setTOKEN(SmartCabinetActivity.this, token);
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
                    XUserData.setTOKEN(SmartCabinetActivity.this, token);
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
}
