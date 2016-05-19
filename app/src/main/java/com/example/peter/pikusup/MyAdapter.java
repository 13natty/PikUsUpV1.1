package com.example.peter.pikusup;

/**
 * Created by Peter on 2016-05-12.
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private final ArrayList mData;

    public MyAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.simplelistbox, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.nameholder)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.tellnumholder)).setText(item.getValue());
        TextView name =(TextView)result.findViewById(R.id.nameholder);
        name.setTextColor(parent.getContext().getResources().getColor(R.color.colorPrimary));
        TextView textview =(TextView)result.findViewById(R.id.tellnumholder);
        textview.setTextColor(parent.getContext().getResources().getColor(R.color.colorPrimary));


        return result;
    }
}