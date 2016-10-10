package com.leontran.themobilevn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.DeviceData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 4/27/2016.
 */
public class ListDeviceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DeviceData> mlistItems = null;
	private View.OnClickListener onClick;
	private LayoutInflater mInflater;
    public ImageLoader mImgLoader;
    public DisplayImageOptions options;

    public ListDeviceAdapter(Context context, ArrayList<DeviceData> items,
                          View.OnClickListener eventOnClick) {
		this.context = context;
        this.mlistItems = items;
        if (this.mlistItems == null) {
            this.mlistItems = new ArrayList<DeviceData>();
        }
		mInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		onClick = eventOnClick;
        mImgLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public ListDeviceAdapter(Context context, ArrayList<DeviceData> items) {
		this.context = context;
        this.mlistItems = items;
        if (this.mlistItems == null) {
            this.mlistItems = new ArrayList<DeviceData>();
        }
		mInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImgLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public void setOnclick(View.OnClickListener onclick) {
		this.onClick = onclick;
    }

    public void setList(ArrayList<DeviceData> items) {
        this.mlistItems.clear();
        this.mlistItems.addAll(items);
        notifyDataSetChanged();
    }

    private LinearLayout layoutMain;
    private ImageView img;
    private TextView txtNumberStore, txtName, txtPrice;

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_device_row, null);
        }
        DeviceData data = mlistItems.get(position);
        layoutMain = (LinearLayout) convertView.findViewById(R.id.layout_main);
        img = (ImageView) convertView.findViewById(R.id.img);
        layoutMain.setOnClickListener(onClick);
        txtName = (TextView) convertView.findViewById(R.id.txt_name);
        txtNumberStore = (TextView) convertView.findViewById(R.id.txt_number_store);
        txtPrice = (TextView) convertView.findViewById(R.id.txt_price);
        layoutMain.setTag(data);
        txtName.setText(data.getName());
        txtNumberStore.setText(data.getStore_count() + "");
        txtPrice.setText(data.getPrice() + " VND");
        mImgLoader.displayImage(data.getImage(), img, options);
        return convertView;
    }

    @Override
    public int getCount() {
        if (mlistItems != null) {
            return mlistItems.size();
        }
        return 0;
    }

    @Override
    public DeviceData getItem(int position) {
        return mlistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

