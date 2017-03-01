package com.sicao.smartwine.xuser;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xhttp.XConfig;

import org.json.JSONException;
import org.json.JSONObject;


public class XUserInfoActivity extends SmartCabinetActivity implements View.OnClickListener {

    //个人头像
    SimpleDraweeView simpleDraweeView;
    //昵称
    TextView nickname;
    //性别
    TextView sex;
    //生日
    TextView birthday;
    //签名
    TextView signtag;

    @Override
    protected int setView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        showProgress(true);
        getUserInfo();
    }

    void init() {
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.avatar);
        nickname = (TextView) findViewById(R.id.nick_name);
        sex = (TextView) findViewById(R.id.sex);
        birthday = (TextView) findViewById(R.id.birthday);
        signtag = (TextView) findViewById(R.id.sign);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.avatar://替换头像

                break;
            case R.id.nick_name://修改昵称

                break;
            case R.id.sex://修改性别

                break;
            case R.id.birthday://修改生日

                break;
            case R.id.sign://修改签名

                break;
        }
    }

    void getUserInfo() {
        xSicaoApi.getUserInfo(this, new XApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    showProgress(false);
                    JSONObject object1 = (JSONObject) object;
                    nickname.setText(object1.getString("nickname"));
                    signtag.setText(TextUtils.isEmpty(object1.getString("signature")) ? "\"这个人很懒，什么也没说\""
                            : object1.getString("signature"));
                    simpleDraweeView.getHierarchy().setPlaceholderImage(
                            getResources()
                                    .getDrawable(R.mipmap.ic_launcher),
                            ScalingUtils.ScaleType.FIT_CENTER);
                    simpleDraweeView.getHierarchy().setFailureImage(
                            getResources()
                                    .getDrawable(R.mipmap.ic_launcher),
                            ScalingUtils.ScaleType.FIT_CENTER);
                    simpleDraweeView.setImageURI(Uri.parse(object1.getString("avatar")));
                    if (object1.getString("sex").equals("f")) {
                        sex.setText("女");
                    } else {
                        sex.setText("男");
                    }
                    birthday.setText(object1.getString("birthday"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                showProgress(false);
                Toast(error);
            }
        });
    }

    @Override
    public void message(Message msg) {
        super.message(msg);
        if (msg.what == XConfig.BASE_UPDATE_ACTION) {
            getUserInfo();
        }
    }
}
