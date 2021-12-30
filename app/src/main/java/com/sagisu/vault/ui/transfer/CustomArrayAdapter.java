package com.sagisu.vault.ui.transfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private Context ctx;
    private String[] contentArray;
    private int layoutName;
    private int textViewId;

    public CustomArrayAdapter(Context context, int resource,int textViewId, String[] objects) {
        super(context,  resource, textViewId, objects);
        this.ctx = context;
        this.contentArray = objects;
        this.layoutName = resource;
        this.textViewId = textViewId;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layoutName, parent, false);

        TextView textView = (TextView) row.findViewById(textViewId);
        textView.setText(contentArray[position]);

        return row;
    }
}
