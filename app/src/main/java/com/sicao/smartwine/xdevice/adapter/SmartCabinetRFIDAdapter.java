package com.sicao.smartwine.xdevice.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.List;

public class SmartCabinetRFIDAdapter extends BaseAdapter {
    Context mContext;
    List<XRfidEntity> mList;
    LayoutInflater mInflater;

    public SmartCabinetRFIDAdapter(Context context, List<XRfidEntity> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setList(List<XRfidEntity> mList) {
        this.mList = mList;
    }

    public void update(List<XRfidEntity> mList) {
        setList(mList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public XRfidEntity getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.adapter_device_list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.device_icon);
            holder.name = (TextView) convertView.findViewById(R.id.textView15);
            holder.tv_equipment = (TextView) convertView.findViewById(R.id.tv_equipment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        XRfidEntity entity = mList.get(position);
        holder.name.setText("标签：" + entity.getRfid());
        if (entity.getTag().equals("current")) {
            holder.tv_equipment.setText("[上次]");
        } else if (entity.getTag().equals("add")) {
            holder.tv_equipment.setText("[增加]");
        } else if (entity.getTag().equals("add")) {
            holder.tv_equipment.setText("[移除]");
        }
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name, tv_equipment;//名字 /当前设备
    }
}
