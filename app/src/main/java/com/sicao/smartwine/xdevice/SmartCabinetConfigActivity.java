package com.sicao.smartwine.xdevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import static com.sicao.smartwine.xdevice.SmartCabinetConfigActivity.ConfigStatus.BIND_ERROR;

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
        mHintText.setVisibility(View.VISIBLE);
    }

    @Override
    public void configSuccess(String mac, String did, String productKey) {
        super.configSuccess(mac, did, productKey);
        mHandler.sendEmptyMessage(ConfigStatus.CONFIG_SUCCESS.ordinal());
        Toast.makeText(this,"设备配置OK,正在绑定该设备",Toast.LENGTH_LONG).show();
        //绑定该设备
        xCabinetApi.bindDevice(XUserData.getCabinetUid(this),XUserData.getCabinetToken(this),mac,getProductKey(),getProductSecret());
        mHandler.sendEmptyMessageDelayed(ConfigStatus.START_BIND.ordinal(),2000);
    }

    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        mHandler.sendEmptyMessageDelayed(ConfigStatus.BIND_SUCCESS.ordinal(),2000);
    }

    @Override
    public void bindError(GizWifiErrorCode result) {
        super.bindError(result);
        Message msg=mHandler.obtainMessage();
        msg.obj=errorCodeToString(result);
        msg.what=ConfigStatus.BIND_ERROR.ordinal();
        mHandler.sendMessageDelayed(msg,2000);
    }

    @Override
    public void configing(String mac, String did, String productKey) {
        super.configing(mac, did, productKey);
        mHandler.sendEmptyMessage(ConfigStatus.CONFIG_ING.ordinal());
    }

    @Override
    public void configError(GizWifiErrorCode result) {
        super.configError(result);
        Toast.makeText(this,errorCodeToString(result),Toast.LENGTH_LONG).show();
        Message msg=mHandler.obtainMessage();
        msg.obj=errorCodeToString(result);
        msg.what=ConfigStatus.CONFIG_ERROR.ordinal();
        mHandler.sendMessage(msg);
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
                                mHandler.sendEmptyMessage(ConfigStatus.START_CONFIG.ordinal());
                                xCabinetApi.configAirLink(scan.SSID,password.getText().toString().trim());
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
    public  enum  ConfigStatus{
        START_CONFIG,//开始配置
        CONFIG_ING,  //配置中
        CONFIG_SUCCESS, //配置OK
        CONFIG_ERROR,//配置失败

        START_BIND, //开始绑定
        BIND_ING, //绑定中
        BIND_SUCCESS, //绑定成功
        BIND_ERROR, //绑定失败
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           ConfigStatus key=ConfigStatus.values()[msg.what];
            switch (key){
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
                    mHintText.setText("设备配置失败,"+msg.obj);
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
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SmartCabinetConfigActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
                            finish();
                        }
                    },2000);
                    break;
                case BIND_ERROR:
                    mHintText.setText("绑定设备失败,"+msg.obj);
                    Toast.makeText(SmartCabinetConfigActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SmartCabinetConfigActivity.this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
                            finish();
                        }
                    },2000);
                    break;
            }
        }
    };
}
