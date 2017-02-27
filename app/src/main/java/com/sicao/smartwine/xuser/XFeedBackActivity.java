package com.sicao.smartwine.xuser;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xhttp.XApiCallBack;

/***
 * 意见反馈
 */
public class XFeedBackActivity extends SmartCabinetActivity {
    // 意见反馈控件
    EditText sug;
    String content = "";

    @Override
    protected int setView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sug = (EditText) findViewById(R.id.et_input);
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("提交");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = sug.getText().toString().trim();
                if (!"".equals(content)) {
                    xSicaoApi.feedBack(XFeedBackActivity.this,
                            content, new XApiCallBack() {
                                @Override
                                public void response(Object object) {
                                    sug.setText("");
                                    Toast("谢谢你的建议,我们将尽快做出修改");
                                    finish();
                                }
                            }, null);
                } else {
                    Toast("请输入反馈内容");
                }
            }
        });
    }
}
