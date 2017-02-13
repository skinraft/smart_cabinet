package com.sicao.smartwine.xwidget.dialog;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;

public class SmartCabinetSettingDialog extends PopupWindow {
    LinearLayout ll_popup;
    View mainview;
    Context context;
    Animation anim_out;
    ListView mListView;
    PopBottomAdapter adapter;
    String[] mData = new String[]{};

    public void setmData(String[] mData) {
        this.mData = mData;
    }

    public void update(String[] data) {
        setmData(data);
        adapter.update(mData);
    }

    public SmartCabinetSettingDialog(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainview = inflater.inflate(R.layout.pop_bottom, null);
        ll_popup = (LinearLayout) mainview.findViewById(R.id.parent);
        mListView = (ListView) mainview.findViewById(R.id.view2);
        adapter = new PopBottomAdapter(context, mData);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != menuItemClickListener)
                    menuItemClickListener.onClick(position,mData[position]);
            }
        });
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(SmartCabinetApplication.metrics.widthPixels * 3 / 5);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setContentView(mainview);
        anim_out = AnimationUtils
                .loadAnimation(context, R.anim.push_bottom_out);
        mainview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        ll_popup.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.push_bottom_in));
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        ll_popup.startAnimation(anim_out);
        anim_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                superDismiss();
            }
        });
    }

    private void superDismiss() {
        super.dismiss();
        anim_out = null;
    }

    public interface MenuItemClickListener {
         void onClick(int  position,String value);
    }

    private MenuItemClickListener menuItemClickListener;

    public void setMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }
}
