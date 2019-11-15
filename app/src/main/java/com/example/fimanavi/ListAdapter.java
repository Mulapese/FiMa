package com.example.fimanavi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends BaseAdapter {

    Context context;
    private String[] name;
    private String[] date;
    private int[] icon;
    private boolean[] selection;

    public ListAdapter() {
    }

    public ListAdapter(Context context, String[] name, String[] date, int[] icon) {
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.name = name;
        this.date = date;
        this.icon = icon;
    }

    public void setData(Context context, String[] data, String[] data1, int[] data2) {
        this.context = context;
        name = data;
        notifyDataSetChanged();

        date = data1;
        notifyDataSetChanged();

        icon = data2;
        notifyDataSetChanged();

    }

    public void setSelection(boolean[] selection) {
        if (selection != null) {
            this.selection = new boolean[selection.length];
            for (int i = 0; i < selection.length; i++) {
                this.selection[i] = selection[i];
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return name[position];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            result = convertView;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtName.setText(name[position]);
        viewHolder.txtDate.setText(date[position]);
        viewHolder.icon.setImageResource(icon[position]);

        // Holder when long press
        ListAdapter.ViewHolder holder = (ListAdapter.ViewHolder) convertView.getTag();
        final String item = (String)getItem(position);
        holder.txtName.setText(item.substring(item.lastIndexOf('/') + 1));
        if (selection != null) {
            if (selection[position]) {
                holder.txtName.setBackgroundColor(Color.argb(100, 202, 221, 237)); // Màu của Item khi được select
                holder.txtDate.setBackgroundColor(Color.argb(100, 202, 221, 237));
                holder.icon.setBackgroundColor(Color.argb(100, 202, 221, 237));
            } else {
                holder.txtName.setBackgroundColor(Color.WHITE);
                holder.txtDate.setBackgroundColor(Color.WHITE);
                holder.icon.setBackgroundColor(Color.WHITE);  // Màu của Item khi bỏ select
            }
        }
        return convertView;

    }

    private static class ViewHolder {

        TextView txtName;
        TextView txtDate;
        ImageView icon;

    }

}