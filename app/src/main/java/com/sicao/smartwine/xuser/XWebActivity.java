package com.sicao.smartwine.xuser;

import android.os.Bundle;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xwidget.TWebView;

/***
 * web页面
 */
public class XWebActivity extends SmartCabinetActivity {
    TWebView tWebView;
    @Override
    protected int setView() {
        return R.layout.activity_xweb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    void init(){
        tWebView= (TWebView) findViewById(R.id.web_view);
        String url=getIntent().getExtras().getString("url");
        tWebView.loadUrl(url);
    }
}
