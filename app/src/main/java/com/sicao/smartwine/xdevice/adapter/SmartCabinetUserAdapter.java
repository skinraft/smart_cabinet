package com.sicao.smartwine.xdevice.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.List;

public class SmartCabinetUserAdapter extends BaseAdapter {
    Context mContext;
    List<GizUserInfo> mList;
    LayoutInflater mInflater;

    public SmartCabinetUserAdapter(Context context, List<GizUserInfo> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setList(List<GizUserInfo> mList) {
        this.mList = mList;
    }

    public void update(List<GizUserInfo> mList) {
        setList(mList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public GizUserInfo getItem(int position) {
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
        final GizUserInfo device = mList.get(position);
        holder.name.setText(device.getUsername());
        holder.tv_equipment.setText("位置:"+device.getAddress()+",绑定时间:"+device.getDeviceBindTime());
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name, tv_equipment;//名字 /当前设备
    }
}
