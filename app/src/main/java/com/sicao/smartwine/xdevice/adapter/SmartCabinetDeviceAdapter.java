package com.sicao.smartwine.xdevice.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.xdata.SmartCabinet;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XConfig;

import java.util.List;

/**
 * Created by techssd on 2016/1/23.
 */
public class SmartCabinetDeviceAdapter extends BaseAdapter {
    Context mContext;
    List<SmartCabinet> mList;
    LayoutInflater mInflater;

    public SmartCabinetDeviceAdapter(Context context, List<SmartCabinet> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater=LayoutInflater.from(mContext);
    }

    public void setList(List<SmartCabinet> mList) {
        this.mList = mList;
    }

    public void update(List<SmartCabinet> mList) {
        setList(mList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SmartCabinet getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null==convertView){
            convertView=mInflater.inflate(R.layout.adapter_device_list_item,null);
            holder=new ViewHolder();
            holder.icon=(ImageView)convertView.findViewById(R.id.device_icon);
            holder.name=(TextView)convertView.findViewById(R.id.textView15);
            holder.tv_equipment=(TextView) convertView.findViewById(R.id.tv_equipment);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        final SmartCabinet device = mList.get(position);
        if (XConfig.DEBUG){
            holder.name.setText("设备名称："+device.getName()+"\n"+"[长按可复制设备编号]");
        }else{
            holder.name.setText("设备名称："+device.getName());
        }
        if (XUserData.getCurrentCabinetId(mContext).equals(device.getId())){
            holder.tv_equipment.setText("当前设备");
        }else{
            holder.tv_equipment.setText("未选择");
        }
        if(device.isOnline()){
            if (XConfig.DEBUG){
                holder.tv_equipment.setText(holder.tv_equipment.getText().toString()+":在线\n"+device.getId());
            }else{
                holder.tv_equipment.setText(holder.tv_equipment.getText().toString()+":在线");
            }
            holder.icon.setImageResource(R.drawable.ic_cupboard);
            holder.tv_equipment.setTextColor(mContext.getResources().getColor(R.color.actionBarColor));
        }else{
            holder.icon.setImageResource(R.drawable.ic_cupboard_gray);
            holder.tv_equipment.setTextColor(Color.parseColor("#3D3D3D"));
            if (XConfig.DEBUG){
                holder.tv_equipment.setText(holder.tv_equipment.getText().toString()+":离线\n"+device.getId());
            }else{
                holder.tv_equipment.setText(holder.tv_equipment.getText().toString()+":离线");
            }
        }
        return convertView;
    }
    class ViewHolder{
        ImageView icon;
        TextView name,tv_equipment;//名字 /当前设备
    }
}
