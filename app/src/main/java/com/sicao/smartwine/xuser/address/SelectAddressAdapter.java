package com.sicao.smartwine.xuser.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sicao.smartwine.R;

import java.util.ArrayList;

public class SelectAddressAdapter extends BaseAdapter {
	private ArrayList<XCityEntity> mlist;
	private Context mContext;
	private LayoutInflater inflater;
	private String mprivince, mcity;

	public SelectAddressAdapter(ArrayList<XCityEntity> list, Context context) {
		this.mlist = list;
		this.mContext = context;
		inflater = LayoutInflater.from(context);
	}

	public void upData(ArrayList<XCityEntity> list, String privince, String city) {
		this.mlist = list;
		mprivince = privince;
		mcity = city;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public XCityEntity getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mlist.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView;
		if (null == convertView) {
			holdView = new HoldView();
			convertView = inflater.inflate(R.layout.select_address_item, null);
			holdView.tv_address = (TextView) convertView
					.findViewById(R.id.tv_address);
			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		// 设置城市名称
		final int index = position;
		holdView.tv_address.setText(mlist.get(position).getProvince());
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mClicks.setOnclick(index);
			}
		});
		return convertView;
	}

	class HoldView {
		TextView tv_address;
	}

	public interface onClicks {
		void setOnclick(int position);
	}

	public onClicks mClicks;

	public void setOnclicks(onClicks Clicks) {
		this.mClicks = Clicks;
	}
}
