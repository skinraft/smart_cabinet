package com.sicao.smartwine.xuser;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;

/**
 * 找回密码界面
 *
 * @author putaoji
 */
public class XRegisterActivity extends SmartCabinetActivity {
    // 手机号控件
    EditText mPhone;
    // 验证码控件
    EditText mCode;
    // 密码控件
    EditText mPassword;
    // 手机号
    String mPhoneNumber = "";
    // 验证码
    String mCodeNumber = "";
    // 密码
    String mPasswordNumber = "";
    // 获取验证码
    TextView mGetCode;
    //
    Handler mHandler;
    // 提交按钮
    TextView mCommit;
    // 布尔值，用于控制完成按钮的背景颜色显示
    boolean phone = false, code = false, password = false;


    @Override
    protected int setView() {
        return R.layout.activity_device_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mCenterTitle.setText("注册");
        // 获取验证码
        mGetCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhone.getText().toString().trim().length() < 11) {
                    Toast("请输入正确的手机号");
                    return;
                }
                // 1，API提交手机号
                mPhoneNumber = mPhone.getText().toString().trim();
                getSMScode(mPhoneNumber);
                // 2，该控件变为不可点击

                mGetCode.setClickable(false);

                // 3，手机号码输入框不可输入

                mPhone.setClickable(false);
                mPhone.setFocusable(false);

                // 4，执行120s倒计时

                countDown();

                // 5，释放提交按钮的控制锁

                mCommit.setTextColor(Color.parseColor("#FFFFFF"));
                mCommit.setClickable(true);
                mCommit.invalidate();

            }
        });
        // 验证码倒计时
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;

                switch (what) {

                    case 10034:
                        int count = msg.arg1;
                        mGetCode.setText(count + "s");
                        // 释放控制锁
                        if (count == 0) {
                            mGetCode.setText("重新获取验证码");
                            mGetCode.setClickable(true);
                        }

                        break;

                    default:

                        break;
                }

            }
        };

        // 提交按钮
        mCommit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    /***
     * 提交手机号和验证码
     */
    public void commit() {
        mCodeNumber = mCode.getText().toString().trim();
        mPhoneNumber = mPhone.getText().toString().trim();
        mPasswordNumber = mPassword.getText().toString().trim();
        if ("".equals(mCodeNumber) || "".equals(mPhoneNumber)
                || "".equals(mPasswordNumber)) {
            Toast("请输入手机号以及验证码的信息");
            return;
        }
        showProgress(true);
        xSicaoApi.register(this, mPhoneNumber, mCodeNumber, mPasswordNumber, new XApiCallBack() {
            @Override
            public void response(Object object) {
                //注册OK----返回登录页面
                Toast("操作成功");
                showProgress(false);
                finish();
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                showProgress(false);
                Toast("操作失败,请重试!");
            }
        });
    }

    /***
     * 初始化控件
     */
    public void init() {
        mPhone = (EditText) findViewById(R.id.x_phone_edit);
        mCode = (EditText) findViewById(R.id.x_code_edit);
        mPassword = (EditText) findViewById(R.id.x_password_edit);
        mGetCode = (TextView) findViewById(R.id.x_code_right);
        mCommit = (TextView) findViewById(R.id.x_login_text);
        // 提交按钮默认为灰色不可点击
        mCommit.setTextColor(Color.parseColor("#EDEDED"));
        mCommit.setClickable(false);
        mCommit.invalidate();
        mPhone.addTextChangedListener(new TextWatcher() {

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
                if (!TextUtils.isEmpty(s) && s.length() == 11) {
                    mGetCode.setBackgroundResource(R.drawable.x_login_red_x);
                    phone = true;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                } else {
                    mGetCode.setBackgroundResource(R.drawable.x_login_gay_x);
                    phone = false;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                }

            }
        });
        mCode.addTextChangedListener(new TextWatcher() {

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
                if (!TextUtils.isEmpty(s) && s.length() == 4) {
                    code = true;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                } else {
                    code = false;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                }

            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {

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
                if (!TextUtils.isEmpty(s) && s.length() >= 6) {
                    password = true;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                } else {
                    password = false;
                    if (phone && code && password) {
                        mCommit.setBackgroundResource(R.drawable.x_login_red_x);
                        mCommit.setClickable(true);
                    } else {
                        mCommit.setBackgroundResource(R.drawable.x_login_gay_x);
                        mCommit.setClickable(false);
                    }
                }

            }
        });
    }

    /***
     * 120s倒计时
     */
    public void countDown() {
        new Thread() {
            public void run() {

                int count = 120;

                while (count >= 0) {

                    Message msg = mHandler.obtainMessage();

                    msg.what = 10034;

                    msg.arg1 = count;

                    mHandler.sendMessage(msg);

                    count--;

                    Thread.currentThread();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    /****
     * 获取短信验证码
     *
     * @param phoneNumber
     */
    public void getSMScode(String phoneNumber) {
        xSicaoApi.getCodeForRegister(this, phoneNumber,"getcodeForRegister", new XApiCallBack() {
            @Override
            public void response(Object object) {
                Toast("验证码已发送至您的手机,请注意查收");
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                Toast(error+" 请重试!");
            }
        });
    }
}
