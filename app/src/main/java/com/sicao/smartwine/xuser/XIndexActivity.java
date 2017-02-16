package com.sicao.smartwine.xuser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sicao.smartwine.R;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiService;

public class XIndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xindex);
        if (!AppManager.isServiceRunning(this, "com.sicao.smartwine.xhttp.XApiService")) {
            startService(new Intent(this, XApiService.class));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(XIndexActivity.this, XLoginActivity.class));
                finish();
            }
        },2000);
    }
}
