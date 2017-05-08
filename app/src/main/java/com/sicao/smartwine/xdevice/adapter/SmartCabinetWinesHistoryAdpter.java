package com.sicao.smartwine.xdevice.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import com.sicao.smartwine.xdevice.entity.XProductHistoryEntity;

import java.util.ArrayList;

public class SmartCabinetWinesHistoryAdpter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<XProductHistoryEntity> mlist;
    private RelativeLayout.LayoutParams params;

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public XProductHistoryEntity getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mlist.get(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == view) {
            view = mInflater.inflate(R.layout.device_history_list, null);
            holder=new ViewHolder();
            holder.tag_action = (TextView) view.findViewById(R.id.tag_action);
            holder.picture = (SimpleDraweeView) view
                    .findViewById(R.id.picture);
            holder.name = (TextView) view
                    .findViewById(R.id.name);
            holder.time = (TextView) view
                    .findViewById(R.id.time);
            holder.tag = (ImageView) view
                    .findViewById(R.id.tag);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final XProductHistoryEntity wineEntity = mlist.get(position);
        holder.time.setText(wineEntity.getDate());
        holder.name.setText(wineEntity.getName());
        if (null == holder.picture.getTag()) {
            holder.picture.setLayoutParams(params);
            holder.picture.setTag(wineEntity);
        }
        holder.picture.getHierarchy().setPlaceholderImage(
                mContext.getResources().getDrawable(
                        R.mipmap.ic_launcher),
                ScalingUtils.ScaleType.FIT_CENTER);
        holder.picture.getHierarchy().setFailureImage(
                mContext.getResources().getDrawable(
                        R.mipmap.ic_launcher),
                ScalingUtils.ScaleType.FIT_CENTER);
        if (!"-1".equals(wineEntity.getId())) {
            if (null != wineEntity.getIcon() && !wineEntity.getIcon().equals("")) {
                Uri uri = Uri.parse(wineEntity.getIcon());
                holder.picture.setImageURI(uri);
            } else {
                holder.picture.getHierarchy()
                        .setFailureImage(
                                mContext.getResources().getDrawable(
                                        R.mipmap.ic_launcher));
            }
        }
        if (null != wineEntity.getTag() && !"".equals(wineEntity.getTag())) {
            if (wineEntity.getTag().equals("add")) {
                holder.tag.setImageResource(R.drawable.ic_in);
                holder.tag_action.setTextColor(Color.parseColor("#ad1c79"));
                holder.tag_action.setText("放入");
            } else {
                holder.tag.setImageResource(R.drawable.ic_out);
                holder.tag_action.setTextColor(Color.parseColor("#3D3D3D"));
                holder.tag_action.setText("取出");
            }
        }
        return view;
    }

    public SmartCabinetWinesHistoryAdpter(Context context, ArrayList<XProductHistoryEntity> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mlist = list;
        params = new RelativeLayout.LayoutParams(
                SmartCabinetApplication.metrics.widthPixels / 4,
                SmartCabinetApplication.metrics.widthPixels / 4);
    }

    public void upDataAdapter(ArrayList<XProductHistoryEntity> list) {
        this.mlist = list;
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        SimpleDraweeView picture;
        TextView name;// 酒名
        // 时间
        TextView time;
        //拿出还是放入
        ImageView tag;
        //存入还是取出
        TextView tag_action;
    }
}
