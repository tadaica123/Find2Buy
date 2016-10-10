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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.DeviceMoreDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 5/10/2016.
 */
public class ListStoreAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DeviceMoreDetail> mlistItems = null;
    private View.OnClickListener onClick;
    private LayoutInflater mInflater;
    public ImageLoader mImgLoader;
    public DisplayImageOptions options;

    public ListStoreAdapter(Context context, ArrayList<DeviceMoreDetail> items,
                             View.OnClickListener eventOnClick) {
        this.context = context;
        this.mlistItems = items;
        if (this.mlistItems == null) {
            this.mlistItems = new ArrayList<DeviceMoreDetail>();
        }
        mInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onClick = eventOnClick;
        mImgLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public ListStoreAdapter(Context context, ArrayList<DeviceMoreDetail> items) {
        this.context = context;
        this.mlistItems = items;
        if (this.mlistItems == null) {
            this.mlistItems = new ArrayList<DeviceMoreDetail>();
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

    public void setList(ArrayList<DeviceMoreDetail> items) {
        this.mlistItems.clear();
        this.mlistItems.addAll(items);
        notifyDataSetChanged();
    }

    private LinearLayout layoutMain;
    private RelativeLayout layoutInformation;
    private TextView txtName, txtPrice;
    private ImageView btnShowMap;

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_list_store_row, null);
        }
        DeviceMoreDetail data = mlistItems.get(position);
        layoutMain = (LinearLayout) convertView.findViewById(R.id.layout_main);
        layoutInformation = (RelativeLayout) convertView.findViewById(R.id.layout_device_information);
        layoutMain.setOnClickListener(onClick);
        layoutInformation.setOnClickListener(onClick);
        txtName = (TextView) convertView.findViewById(R.id.txt_name);
        txtPrice = (TextView) convertView.findViewById(R.id.txt_price);
        btnShowMap = (ImageView) convertView.findViewById(R.id.btn_show_in_map);
        btnShowMap.setOnClickListener(onClick);
        btnShowMap.setTag(data);
        layoutMain.setTag(data);
        layoutInformation.setTag(data);
        txtName.setText(data.getStore());
        txtPrice.setText(data.getPrice() + " VND");
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
    public DeviceMoreDetail getItem(int position) {
        return mlistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}


