package com.sicao.smartwine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

public class SmartCabinetActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                } else {
                    // 注册失败
                }
            }

            @Override
            public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
                super.didUserLogin(result, uid, token);
                //用户登录回调
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 登录成功
                } else {
                    // 登录失败
                }
            }

            @Override
            public void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
                //获取验证码
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 请求成功
                } else {
                    // 请求失败
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
     *
     * @return appSecret
     */
    public native String appSecretFromJNI();
}
