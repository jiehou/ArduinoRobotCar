package com.filderbaer.arduinocarconsole;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceListBaseAdapter extends BaseAdapter {
	
	private ArrayList<Device> deviceArrayList;
	private LayoutInflater mInflater;
	
	public DeviceListBaseAdapter(Context context, ArrayList<Device> devices){
		deviceArrayList = devices;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return deviceArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.device_row_view, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
			holder.tvSignal = (TextView) convertView.findViewById(R.id.tvSignal);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvName.setText(deviceArrayList.get(position).getName());
		holder.tvAddress.setText(deviceArrayList.get(position).getAddress());
		if(!deviceArrayList.get(position).getSignal().equals("0"))
		{
		holder.tvSignal.setText(deviceArrayList.get(position).getSignal() + "dBm");
		}
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView tvName;
		TextView tvAddress;
		TextView tvSignal;
	}
}
