package com.sicao.smartwine.xdevice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xuser.XIndexActivity;

import java.util.List;

/***
 * 配置设备
 */
public class SmartCabinetConfigActivity extends SmartCabinetActivity implements View.OnClickListener {
    //WIFI  SSID
    TextView SSID;
    //WIFI  password
    EditText password;
    //next page
    TextView nextPage;
    //look  password
    ImageView lookPassword;
    //密码是否正在显示
    boolean passwordShow = false;
    WifiManager wifi = null;
    //6.0系统权限检测
    boolean allow = false;

    @Override
    public int setView() {
        return R.layout.activity_device_config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SSID = (TextView) findViewById(R.id.editText1);
        WifiManager wifi = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        if (null != wifiInfo) {
            SSID.setText(wifiInfo.getSSID());
        }
        password = (EditText) findViewById(R.id.editText2);
        nextPage = (TextView) findViewById(R.id.textView10);
        lookPassword = (ImageView) findViewById(R.id.open_pwd);
        nextPage.setOnClickListener(this);
        lookPassword.setOnClickListener(this);
        mHintText.setVisibility(View.VISIBLE);
        //权限检测
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, XConfig.ACCESS_FINE_LOCATION_CODE);
            allow = false;
        } else {
            allow = true;
        }
    }

    @Override
    public void configSuccess(String mac, String did, String productKey) {
        super.configSuccess(mac, did, productKey);
        handler.sendEmptyMessage(ConfigStatus.CONFIG_SUCCESS.ordinal());
        Toast("设备配置OK,正在绑定该设备");
        //绑定该设备
        xCabinetApi.bindDevice(XUserData.getCabinetUid(this), XUserData.getCabinetToken(this), mac, getProductKey(), getProductSecret());
        handler.sendEmptyMessageDelayed(ConfigStatus.START_BIND.ordinal(), 2000);
    }

    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        handler.sendEmptyMessageDelayed(ConfigStatus.BIND_SUCCESS.ordinal(), 2000);
    }

    @Override
    public void bindError(GizWifiErrorCode result) {
        super.bindError(result);
        Message msg = handler.obtainMessage();
        msg.obj = errorCodeToString(result);
        msg.what = ConfigStatus.BIND_ERROR.ordinal();
        handler.sendMessageDelayed(msg, 2000);
    }

    @Override
    public void configing(String mac, String did, String productKey) {
        super.configing(mac, did, productKey);
        handler.sendEmptyMessage(ConfigStatus.CONFIG_ING.ordinal());
    }

    @Override
    public void configError(GizWifiErrorCode result) {
        super.configError(result);
        Toast(errorCodeToString(result));
        //配置失败，
        Message msg = handler.obtainMessage();
        msg.obj = errorCodeToString(result);
        msg.what = ConfigStatus.CONFIG_ERROR.ordinal();
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.textView10://下一步
                if (allow)
                    try {
                        ///
                        if (!TextUtils.isEmpty(SSID.getText().toString().trim()) &&
                                !TextUtils.isEmpty(password.getText().toString().trim())) {
                            wifi = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            WifiInfo wifiInfo = wifi.getConnectionInfo();
                            if (null == wifiInfo) {
                                Toast("请正确配置当前WIFI信息");
                                return;
                            }
                            SSID.setText(wifiInfo.getSSID());
                            List<ScanResult> scanResults = wifi.getScanResults();
                            for (ScanResult scan : scanResults) {
                                if (wifiInfo.getSSID().contains(scan.SSID) && wifiInfo.getBSSID().contains(scan.BSSID)) {
                                    //配置设备入网
                                    showProgress(true);
                                    handler.sendEmptyMessage(ConfigStatus.START_CONFIG.ordinal());
                                    xCabinetApi.configAirLink(scan.SSID, password.getText().toString().trim());
                                    return;
                                }
                            }

                        } else {
                            Toast("请正确配置当前WIFI信息");
                        }
                    } catch (Exception e) {

                    }
                else
                    Toast.makeText(SmartCabinetConfigActivity.this, "本应用需要获取位置权限哦!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.open_pwd://隐藏和查看密码
                if (!passwordShow) {
                    password
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordShow = true;
                    lookPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_viewed_white));
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordShow = false;
                    lookPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_viewed_gray));
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifi = null;
    }

    public enum ConfigStatus {
        START_CONFIG,//开始配置
        CONFIG_ING,  //配置中
        CONFIG_SUCCESS, //配置OK
        CONFIG_ERROR,//配置失败

        START_BIND, //开始绑定
        BIND_ING, //绑定中
        BIND_SUCCESS, //绑定成功
        BIND_ERROR, //绑定失败
    }

    @Override
    public void message(Message msg) {
        if (msg.what != XConfig.BASE_UPDATE_ACTION && msg.what != XConfig.BASE_LOAD_ACTION) {
            ConfigStatus key = ConfigStatus.values()[msg.what];
            switch (key) {
                case START_CONFIG:
                    mHintText.setText("开始配置设备...");
                    break;
                case CONFIG_ING:
                    mHintText.setText("正在配置设备...");
                    break;
                case CONFIG_SUCCESS:
                    mHintText.setText("设备配置成功...");
                    break;
                case CONFIG_ERROR:
                    mHintText.setText("设备配置失败," + msg.obj);
                    finish();
                    break;
                case START_BIND:
                    mHintText.setText("开始绑定设备...");
                    break;
                case BIND_ING:
                    mHintText.setText("正在绑定设备...");
                    break;
                case BIND_SUCCESS:
                    mHintText.setText("绑定设备成功...");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SmartCabinetConfigActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
                            finish();
                        }
                    }, 2000);
                    break;
                case BIND_ERROR:
                    mHintText.setText("绑定设备失败,请联系设备主帐号已获取绑定权限!" );
                    Toast((String) msg.obj);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SmartCabinetConfigActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
                            finish();
                        }
                    }, 2000);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == XConfig.ACCESS_FINE_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                allow = true;
            } else {
                // Permission Denied
                Toast.makeText(SmartCabinetConfigActivity.this, "本应用需要获取位置权限哦!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
