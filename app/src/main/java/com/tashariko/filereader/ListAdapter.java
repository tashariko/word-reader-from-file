package com.tashariko.filereader;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tashariko on 07/03/17.
 */

public class ListAdapter extends ArrayAdapter<ListModel> {

    private LayoutInflater layoutInflater;
    private ArrayList<ListModel> list;

    public ListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<ListModel> list) {
        super(context, resource);
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_home, null);

            holder.text = (TextView) convertView.findViewById(R.id.detail);
            holder.listtitle = (TextView) convertView.findViewById(R.id.listTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(list.get(position).word + "      " + list.get(position).cnt);
        holder.listtitle.setText(list.get(position).listTitle);

        if (list.get(position).show) {
            holder.listtitle.setVisibility(View.VISIBLE);
        } else {
            holder.listtitle.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class ViewHolder {
        TextView text, listtitle;
    }
}
