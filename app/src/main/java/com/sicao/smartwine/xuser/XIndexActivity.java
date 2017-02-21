package com.sicao.smartwine.xuser;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jrummyapps.android.widget.AnimatedSvgView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xhttp.XApiService;


public class XIndexActivity extends Activity {
    AnimatedSvgView animatedSvgView;
    ImageView mIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xindex);
        mIcon= (ImageView) findViewById(R.id.icon);
        mIcon.startAnimation(AnimationUtils.loadAnimation(this,R.anim.warn_dialog_anim_enter));
        if (!AppManager.isServiceRunning(this, "com.sicao.smartwine.xhttp.XApiService")) {
            startService(new Intent(this, XApiService.class));
        }
        animatedSvgView= (AnimatedSvgView) findViewById(R.id.animated_svg_view);
        animatedSvgView.setViewportSize(SmartCabinetApplication.metrics.widthPixels*2/3,SmartCabinetApplication.metrics.heightPixels/3);
        animatedSvgView.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(XIndexActivity.this, XLoginActivity.class));
                finish();
            }
        },3000);
}
}
