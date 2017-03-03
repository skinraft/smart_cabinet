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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xdevice.entity.XGoodsEntity;
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
            mView.textView1 = (TextView) convertView
                    .findViewById(R.id.textView1);
            mView.rr_jiondetails = (RelativeLayout) convertView
                    .findViewById(R.id.rr_jiondetails);
            mView.activityPrice = (TextView) convertView
                    .findViewById(R.id.textView2);
            mView.tv_mywine_englishname = (TextView) convertView
                    .findViewById(R.id.tv_wine_englishname);
            mView.num = (TextView) convertView
                    .findViewById(R.id.textView3);
            convertView.setTag(mView);
        } else {
            mView = (HoldView) convertView.getTag();
        }
        final XWineEntity wineEntity = mlist.get(position);
        final XGoodsEntity  entity=wineEntity.getxGoodsEntity();
        if (null == mView.iv_mycellarimage.getTag()) {
            mView.iv_mycellarimage.setLayoutParams(params);
            mView.iv_mycellarimage.setTag(entity);
        }
        
        mView.num.setText(entity.getMax_bought() + "支酒");
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
        if (!TextUtils.isEmpty(entity.getEnglish_name())) {
            mView.tv_mywine_englishname.setVisibility(View.VISIBLE);
            mView.tv_mywine_englishname.setText(entity.getEnglish_name());
        } else {
            mView.tv_mywine_englishname.setVisibility(View.GONE);
        }
        mView.activityPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mView.activityPrice.setText("￥" + entity.getOrigin_price());
        if (!entity.getFeatured_price().equals("0.00")) {
            Drawable drawable = mContext.getResources().getDrawable(
                    R.drawable.ic_promotion);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            mView.textView1.setCompoundDrawables(drawable, null, null, null);
            DecimalFormat df = new DecimalFormat("###.00");
            mView.textView1.setText(df.format(Double
                    .parseDouble(entity.getFeatured_price())));
        } else {
            mView.textView1.setCompoundDrawables(null, null, null, null);
            mView.textView1.setText("￥ " + entity.getCurrent_price());
        }
        return convertView;
    }

    // 口袋类
    class HoldView {
        SimpleDraweeView iv_mycellarimage;
        TextView tv_mywinename, textView1, tv_mywine_englishname;// 酒名
        // 酒的价格/酒的英文名字
        RelativeLayout rr_jiondetails;
        // 活动优惠价格
        TextView activityPrice;
        //总共有几支酒
        TextView num;
    }
}