package com.leontran.themobilevn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leontran.themobilevn.model.SimpleObjectType;

import java.util.List;

/**
 * Created by NguyenTa.Tran on 7/28/2016.
 */
public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<SimpleObjectType> itemList;

    public CustomSpinnerAdapter(Context context, List<SimpleObjectType> itemList) {
        super();
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public String getItemStringId(int i){
        return itemList.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent,
                false);
        TextView v = (TextView) row.findViewById(android.R.id.text1);
        v.setText(itemList.get(position).getName());
        return row;
    }


}
