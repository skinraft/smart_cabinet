package com.sicao.smartwine.xuser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApi;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.XSmartCabinetDeviceInfoActivity;
import com.sicao.smartwine.xhttp.XApiService;
import com.sicao.smartwine.xhttp.XConfig;

public class XIndexActivity extends Activity {

    ImageView mIcon;

    SmartCabinetApi xCabinetApi = new SmartCabinetApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xindex);
        GizWifiSDK.sharedInstance().startWithAppID(getApplicationContext(), "57368a09e0b847a39e40469f88c06782");
        GizWifiSDK.sharedInstance().setListener(mGizListener);
        overridePendingTransition(R.anim.activity_out_anim, R.anim.activity_in_anim);// 淡出淡入动画效果
        mIcon = (ImageView) findViewById(R.id.icon);
        mIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.xindex_icon_anim_enter));
        if (!AppManager.isServiceRunning(this, "com.sicao.smartwine.xhttp.XApiService")) {
            startService(new Intent(this, XApiService.class));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, XConfig.WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            login();
        }

    }

    void login() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!"".equals(XUserData.getPassword(XIndexActivity.this))) {
                    if (!"".equals(XUserData.getUserName(XIndexActivity.this))) {
                        //执行登录
                        xCabinetApi.login("sicao-" + XUserData.getUID(XIndexActivity.this), XUserData.getPassword(XIndexActivity.this));
                    }
                } else {
                    startActivity(new Intent(XIndexActivity.this, XLoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    private GizWifiSDKListener mGizListener = new GizWifiSDKListener() {
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);
            //用户登录回调
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 登录成功
                XUserData.saveCabinetUid(XIndexActivity.this, uid);
                XUserData.saveCabinetToken(XIndexActivity.this, token);
                loginSuccess();
            } else {
                // 登录失败
                loginError(result);
            }
            SmartSicaoApi.log("Version=" + GizWifiSDK.sharedInstance().getVersion());
        }

    };

    public void loginSuccess() {
        startActivity(new Intent(this, XSmartCabinetDeviceInfoActivity.class));
        finish();
    }

    public void loginError(GizWifiErrorCode result) {
        startActivity(new Intent(this, XLoginActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == XConfig.WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                login();
            } else {
                // Permission Denied
                Toast.makeText(XIndexActivity.this, "本应用需要该权限哦!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
