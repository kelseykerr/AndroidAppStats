package com.kelseykerr.androidappstats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelseykerr.androidappstats.models.AppItem;

import java.util.ArrayList;

/**
 * Created by kelseykerr on 9/21/17.
 */

public class AppItemAdapter extends ArrayAdapter<AppItem> implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<AppItem> appItems;
    Context mContext;

    private static class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
    }

    public AppItemAdapter(ArrayList<AppItem> appItems, Context context) {
        super(context, R.layout.app_item, appItems);
        this.appItems = appItems;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        AppItem appItem = (AppItem) object;
        Log.d(TAG, "Clicked on [" + appItem.getAppName() + "]");
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppItem appItem = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.app_item, parent, false);
            viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.packageName = (TextView) convertView.findViewById(R.id.package_name);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.appIcon.setImageDrawable(appItem.getAppIcon());
        viewHolder.appName.setText(appItem.getAppName());
        viewHolder.packageName.setText(appItem.getPackageName());
        return convertView;
    }
}
