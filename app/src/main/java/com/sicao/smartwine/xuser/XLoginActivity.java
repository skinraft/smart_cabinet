package com.sicao.smartwine.xuser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.SmartCabinetDeviceInfoActivity;
import com.sicao.smartwine.xhttp.XApiCallBack;

public class XLoginActivity extends SmartCabinetActivity {
    // 用户名
    EditText mUsername;
    // 用户密码
    EditText mPassword;
    // 清除账号的按钮
    ImageView mCleanUsername;
    // 查看密码的按钮
    ImageView mSeePassword;
    // 密码是否正在显示
    boolean passwordShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mCenterTitle.setText("登录");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //登录记录
        if (!"".equals(XUserData.getCabinetUid(this))) {
            mUsername.setText(XUserData.getUserName(this));
            mPassword.setText(XUserData.getPassword(this));
            //执行登录
            showProgress(true);
            xCabinetApi.login("sicao-" + XUserData.getUID(XLoginActivity.this), XUserData.getPassword(this));
        }
    }
    @SuppressWarnings("deprecation")
    private void initView() {
        mCenterTitle.setText("登录");
        // 2016 3 8
        mUsername = (EditText) findViewById(R.id.x_phone_edit);
        mPassword = (EditText) findViewById(R.id.x_password_edit);
        mCleanUsername = (ImageView) findViewById(R.id.x_phone_right);
        mCleanUsername.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setText("");
            }
        });

        mSeePassword = (ImageView) findViewById(R.id.x_password_right);
        mSeePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordShow) {
                    mPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordShow = true;
                } else {
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordShow = false;
                }
            }
        });
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("注册");
        mRightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XLoginActivity.this,
                        XRegisterActivity.class);
                startActivity(intent);
            }
        });
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mCleanUsername.setClickable(false);
                    mCleanUsername.setVisibility(View.GONE);
                } else {
                    mCleanUsername.setClickable(true);
                    mCleanUsername.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_login_by_psw;
    }

    /**
     * 登陆按钮点击事件
     *
     * @param v
     */
    public void login(View v) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        showProgress(true);
        login(username, password);
    }

    /**
     * 找回密码按钮点击事件
     *
     * @param v
     */
    public void findPassword(View v) {
        startActivity(new Intent(this, XFindPasswordActivity.class));
    }
    /***
     * 账号密码登录
     *
     * @param username
     * @param password
     */
    public void login(final String username, final String password) {
        showProgress(true);
        //登录
        xSicaoApi.login(this, username, password, new XApiCallBack() {
            @Override
            public void response(Object object) {
                xCabinetApi.register("sicao-" + XUserData.getUID(XLoginActivity.this), password);
            }
        });
    }
    @Override
    public void loginSuccess() {
        super.loginSuccess();
        showProgress(false);
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SmartCabinetDeviceInfoActivity.class));
        finish();
    }

    @Override
    public void loginError(GizWifiErrorCode result) {
        super.loginError(result);
        showProgress(false);
        Toast.makeText(this, "登录失败,请检查您输入的帐号密码是否有误！", Toast.LENGTH_LONG).show();
    }

    @Override
    public void registerSuccess() {
        super.registerSuccess();
        //执行登录动作
        xCabinetApi.login("sicao-" + XUserData.getUID(XLoginActivity.this), mPassword.getText().toString().trim());
    }

    @Override
    public void registerError(GizWifiErrorCode result) {
        super.registerError(result);
        showProgress(false);
        Toast.makeText(this, "注册失败,请重试！" , Toast.LENGTH_LONG).show();
    }
}
