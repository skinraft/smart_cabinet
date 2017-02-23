package com.sicao.smartwine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jrummyapps.android.widget.AnimatedSvgView;

public class Main2Activity extends AppCompatActivity {
    AnimatedSvgView animatedSvgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        animatedSvgView= (AnimatedSvgView) findViewById(R.id.animated_svg_view);
        animatedSvgView.setViewportSize(SmartCabinetApplication.metrics.widthPixels*2/3,SmartCabinetApplication.metrics.heightPixels/3);
        animatedSvgView.start();
    }
}
