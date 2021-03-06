/**
 * Administrator
 * 2014-10-15
 */
package com.sicao.smartwine.xwidget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;

/**
 * @author li'mingqi
 */
public class XWarnDialog extends Dialog implements OnClickListener {
    private Context mContext;
    private TextView mMakeSure, mColse, mContent, mTitle;
    private LayoutInflater mInflater;
    private OnClickListener listener;
    // name
    private EditText mName;
    View view;
    public XWarnDialog(Context context) {
        super(context, R.style.warndialog);
        init(context, "");
    }
    /**
     * li'mingqi 2014-10-15
     */
    @SuppressLint("InflateParams")
    private void init(Context context, String message) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.warn_dialog, null);
        mName = (EditText) view.findViewById(R.id.name);
        mMakeSure = (TextView) view.findViewById(R.id.yes);
        mColse = (TextView) view.findViewById(R.id.no);
        mMakeSure.setOnClickListener(this);
        mColse.setOnClickListener(this);
        mContent = (TextView) view.findViewById(R.id.content);
        if (!"".equals(message) && null != message){
            mContent.setText(message);
        }
        mTitle = (TextView) view.findViewById(R.id.title);
        addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.width= SmartCabinetApplication.metrics.widthPixels*3/4;
        getWindow().setAttributes(params);
    }
    public void setContent(String text) {
        if (!"".equals(text) && null != text) {
            mContent.setText(text);
            mContent.invalidate();
            view.invalidate();
        }
    }

    public void setMakeSure(String makeSure){
        mMakeSure.setText(makeSure);
    }

    public void setmColse(String close) {
        mColse.setText(close);
    }

    public void setTitle(String title) {
        if (!"".equals(title) && null != title) {
            mTitle.setText(title);
        }
        mTitle.setText(title);
    }
    public String getText() {
        String content = mContent.getText().toString();
        if (null == content) {
            content = "";
        }
        return content;
    }

    /**
     * @param listener the listener to set
     */
    public void setOnListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.yes:
                if (null != listener) {
                    this.listener.makeSure();
                } else {
                    dismiss();
                }
                break;

            case R.id.no:
                if (null != listener) {
                    this.listener.cancle();
                } else {
                    dismiss();
                }
                break;
        }
    }

    /***
     * 弹出
     */
    public void show() {
        super.show();

    }

    /***
     * 消失
     */
    public void dismiss() {
        super.dismiss();
    }

    public interface OnClickListener {
        void makeSure();

        void cancle();

    }
}
