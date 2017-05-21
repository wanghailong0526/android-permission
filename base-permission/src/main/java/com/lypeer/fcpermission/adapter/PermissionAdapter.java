package com.lypeer.fcpermission.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lypeer.fcpermission.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3/0003.
 */

public class PermissionAdapter extends BaseAdapter {
    private final List<String> strings;
    private final Context context;
    private LayoutInflater inflater;
    private List<Integer> pictures;

    public PermissionAdapter(Context context, List<Integer> pictures, List<String> strings) {
        super();
        this.pictures = pictures;

        this.strings = strings;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        if (null != pictures) {
            return pictures.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_view_fcpermission_dialog, null);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(pictures.get(position));
        viewHolder.text.setText(strings.get(position));
        return convertView;
    }

}

class ViewHolder {
    public TextView text;
    public ImageView image;
}