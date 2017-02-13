package com.sicao.smartwine.xwidget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sicao.smartwine.R;

/**
 * Created by techssd on 2015/12/30.
 */
public class PopBottomAdapter extends BaseAdapter {

    String[] mData;
    LayoutInflater mInflater;
    Context mContext;

    public PopBottomAdapter(Context context, String[] data) {
        this.mContext = context;
        setmData(data);
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setmData(String[] mData) {
        this.mData = mData;
    }

    public void update(String[] data) {
        setmData(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public String getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return mData[position].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.pop_bottom_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.textView14);
            convertView.setTag(holder);
        } else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.name.setText(mData[position]);
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
