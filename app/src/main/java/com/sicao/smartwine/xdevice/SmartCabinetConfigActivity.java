package com.sicao.smartwine.xdevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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

    @Override
    public int setView() {
        return R.layout.activity_device_config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SSID = (TextView) findViewById(R.id.editText1);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        if (null != wifiInfo) {
            SSID.setText(wifiInfo.getSSID());
        }
        password = (EditText) findViewById(R.id.editText2);
        nextPage = (TextView) findViewById(R.id.textView10);
        lookPassword = (ImageView) findViewById(R.id.open_pwd);
        nextPage.setOnClickListener(this);
        lookPassword.setOnClickListener(this);
    }

    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        /***
         * 绑定成功，
         */
        showProgress(false);
        startActivity(new Intent(this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
        finish();
    }

    @Override
    public void bindError(GizWifiErrorCode result) {
        super.bindError(result);
        /***
         * 绑定失败
         */
        showProgress(false);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
        finish();
    }

    @Override
    public void startConfig(String mac, String did, String productKey) {
        super.startConfig(mac, did, productKey);
        //绑定该设备
        xCabinetApi.bindDevice(XUserData.getCabinetUid(this), XUserData.getCabinetToken(this), mac, getProductKey(), getProductSecret());
        Toast.makeText(this, "设备配置OK,正在绑定该设备", Toast.LENGTH_LONG).show();
    }

    @Override
    public void configing(String mac, String did, String productKey) {
        super.configing(mac, did, productKey);
        /***
         * 配置中
         */
    }

    @Override
    public void configError(GizWifiErrorCode result) {
        super.configError(result);
        /***
         * 配置异常
         */
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.textView10://下一步
                try {
                    ///
                    if (!TextUtils.isEmpty(SSID.getText().toString().trim()) &&
                            !TextUtils.isEmpty(password.getText().toString().trim())) {
                        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifi.getConnectionInfo();
                        if (null == wifiInfo) {
                            Toast.makeText(SmartCabinetConfigActivity.this, "请正确配置当前WIFI信息", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SSID.setText(wifiInfo.getSSID());
                        List<ScanResult> scanResults = wifi.getScanResults();
                        for (ScanResult scan : scanResults) {
                            if (wifiInfo.getSSID().contains(scan.SSID) && wifiInfo.getBSSID().contains(scan.BSSID)) {
                                //配置设备入网
                                showProgress(true);
                                xCabinetApi.configAirLink(scan.SSID, password.getText().toString().trim());
                                return;
                            }
                        }

                    } else {
                        Toast.makeText(SmartCabinetConfigActivity.this, "请正确配置当前WIFI信息", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
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
}
