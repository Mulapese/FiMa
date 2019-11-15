package com.example.fimanavi;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextAdapter extends BaseAdapter {
    private List<String> data = new ArrayList<>();
    private boolean[] selection;

    public void setData(List<String> data) {
        if (data != null) {
            this.data.clear();
            if (data.size() > 0) {
                this.data.addAll(data);
            }
            notifyDataSetChanged();
        }
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
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.textItem)));
        }

        // Holder when long press
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final String item = getItem(position);
        holder.info.setText(item.substring(item.lastIndexOf('/') + 1));
        if (selection != null) {
            if (selection[position]) {
                holder.info.setBackgroundColor(Color.argb(100, 202, 221, 237)); // Màu của Item khi được select
            } else {
                holder.info.setBackgroundColor(Color.WHITE); // Màu của Item khi bỏ select
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView info;

        ViewHolder(TextView info) {
            this.info = info;
        }
    }
}