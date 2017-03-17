package com.sicao.smartwine.xdevice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xdevice.entity.XProductEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
public class SmartCabinetWinesAdpter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<XWineEntity> mlist;
    private RelativeLayout.LayoutParams params;

    public SmartCabinetWinesAdpter(Context context, ArrayList<XWineEntity> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mlist = list;
        params = new RelativeLayout.LayoutParams(
                SmartCabinetApplication.metrics.widthPixels / 4,
                SmartCabinetApplication.metrics.widthPixels / 4);
    }

    public void upDataAdapter(ArrayList<XWineEntity> list) {
        this.mlist = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public XWineEntity getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mlist.get(position).hashCode();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoldView mView;
        if (null == convertView) {
            mView = new HoldView();
            convertView = mInflater.inflate(R.layout.device_goods_list, null);
            mView.iv_mycellarimage = (SimpleDraweeView) convertView
                    .findViewById(R.id.iv_mycellarimage);
            mView.tv_mywinename = (TextView) convertView
                    .findViewById(R.id.tv_mywinename);
            mView.price = (TextView) convertView
                    .findViewById(R.id.textView1);
            mView.rr_jiondetails = (RelativeLayout) convertView
                    .findViewById(R.id.rr_jiondetails);
            mView.num = (TextView) convertView
                    .findViewById(R.id.textView3);
            convertView.setTag(mView);
        } else {
            mView = (HoldView) convertView.getTag();
        }
        final XWineEntity wineEntity = mlist.get(position);
        final XProductEntity entity=wineEntity.getProduct();
        if (null == mView.iv_mycellarimage.getTag()) {
            mView.iv_mycellarimage.setLayoutParams(params);
            mView.iv_mycellarimage.setTag(entity);
        }
        mView.num.setText(wineEntity.getRfidnum() + "支酒");
        mView.iv_mycellarimage.getHierarchy().setPlaceholderImage(
                mContext.getResources().getDrawable(
                        R.mipmap.ic_launcher),
                ScalingUtils.ScaleType.FIT_CENTER);
        mView.iv_mycellarimage.getHierarchy().setFailureImage(
                mContext.getResources().getDrawable(
                        R.mipmap.ic_launcher),
                ScalingUtils.ScaleType.FIT_CENTER);
        if (null != entity.getIcon() && !entity.getIcon().equals("")) {
            Uri uri = Uri.parse(entity.getIcon());
            mView.iv_mycellarimage.setImageURI(uri);
        } else {
            mView.iv_mycellarimage.getHierarchy()
                    .setFailureImage(
                            mContext.getResources().getDrawable(
                                    R.mipmap.ic_launcher));
        }
        mView.tv_mywinename.setText(entity.getName());
        mView.price.setText("￥" + entity.getCurrent_price());
        return convertView;
    }

    // 口袋类
    class HoldView {
        SimpleDraweeView iv_mycellarimage;
        TextView tv_mywinename;// 酒名
        // 酒的价格/酒的英文名字
        RelativeLayout rr_jiondetails;
        // 活动优惠价格
        TextView price;
        //总共有几支酒
        TextView num;
    }
}
