package com.sicao.smartwine.xdevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;

/***
 * 綁定/解綁设备
 */
public class SmartCabinetBindStatusActivity extends SmartCabinetActivity {
    //设备状态   1---添加成功 2---添加失败 3---解绑成功  4---解绑失败
    String status = "";
    ImageView tv_connect_icon;
    TextView tv_status, tv_status_prompt, tv_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getIntent().getExtras().getString("status");
        init(status);
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_bind;
    }

    public void init(final String status) {
        tv_connect_icon = (ImageView) findViewById(R.id.tv_connect_icon);
        tv_status = (TextView) findViewById(R.id.x_device_status);
        tv_status_prompt = (TextView) findViewById(R.id.tv_status_prompt);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        if (status.equals("1")) {
            tv_status.setText("绑定成功");
            tv_complete.setText("完成");
            tv_status_prompt.setText("现在您可以使用手机控制您的设备！");
            tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
        } else if (status.equals("2")) {
            tv_status.setText("绑定失败");
            tv_complete.setText("确认");
            tv_status_prompt.setText("请检查设备是否运行正常。若设备运行正常，请联系设备管理员并确认是否已获取绑定权限。");
            tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail));
        } else if (status.equals("3")) {
            tv_status.setText("解绑成功");
            tv_complete.setText("确认");
            tv_status_prompt.setText("您的设备已经解绑成功，欢迎您再次使用！");
            tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
        } else if (status.equals("4")) {
            tv_status.setText("解绑失败");
            tv_complete.setText("确认");
            tv_status_prompt.setText("您的设备解绑失败，请检查网络后重试!");
            tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail));
        }
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("SELECT_NEW_DEVICE"));
                finish();
            }
        });
    }
}
