package com.sicao.smartwine.xuser.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加收货地址页面
 */
public class XAddAddressActivity extends SmartCabinetActivity {
     EditText name, phone;
     TextView address;
     String addresses;
     EditText et_detail_address;

    @Override
    protected int setView() {
        return R.layout.activity_add_address;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        RelativeLayout tv_select_address = (RelativeLayout) findViewById(R.id.tv_select_address);
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);// 详细地址信息
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.user_phone);
        address = (TextView) findViewById(R.id.user_address);
        phone.setInputType(InputType.TYPE_CLASS_PHONE);// 控制电话输入
        tv_select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到选择地址页面
                Intent intent = new Intent(XAddAddressActivity.this,
                        XSelectedItemActivity.class);
                startActivityForResult(intent, 110);
            }
        });
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("提交");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String n = name.getText().toString().trim();
                    String p = phone.getText().toString().trim();
                    String ad = address.getText().toString().trim();
                    String ea = et_detail_address.getText().toString().trim();
                    int pf = p.indexOf(0);
                    if ("".equals(n)) {
                        Toast("姓名不能为空");
                        if (n.length() > 10) {
                            Toast( "请输入正确的名字");
                            return;
                        }
                    }
                    if ("".equals(p)) {
                        Toast( "联系电话不能为空");
                        return;
                    }
                    if (p.length() > 12 || p.length() < 10 && pf != 1) {
                        Toast( "请输入正确的手机号");
                        return;
                    }
                    if ("".equals(ad)) {
                        Toast("地址不能为空");
                        return;
                    }
                    if ("".equals(ea)) {
                        Toast( "详细地址不能为空");
                        return;
                    }
                    String address = ad + ea;
                    addAddress(n, p, address);
            }
        });
    }

    // 回传值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 110 && null != data) {
            addresses = data.getExtras().getString("address");
            address.setText(addresses);
        }

    }

    /**
     * 添加收货地址
     *
     * @param
     */
    public void addAddress(final String name, final String phone,
                           final String address) {
        swipeRefreshLayout.setRefreshing(true);
        xSicaoApi.addAddress(this, name, phone, address, new XApiCallBack() {
            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                Toast("操作成功");
                finish();
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                Toast(error);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
