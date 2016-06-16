package com.android.residentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.residentapp.model.Feed;

import java.util.List;

public class FeedListAdapter extends ArrayAdapter<Feed> {
    private class ViewHolder {
        TextView titleTextView;
    }

    private LayoutInflater inflater;

    public FeedListAdapter(Context context, List<Feed> objects) {
        super(context, 0, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    @Override
//    public boolean isEnabled(int position) {
//        return true;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_feed, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final Feed data = getItem(position);
            final String title = data.title;
            holder.titleTextView.setText(title);

        } catch (Exception e) {
//			throw new RuntimeException(e); //FIXME
            e.printStackTrace();
        }

        return convertView;
    }
}
