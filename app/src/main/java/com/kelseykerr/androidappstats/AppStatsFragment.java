package com.kelseykerr.androidappstats;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kelseykerr.androidappstats.models.AppStats;

/**
 * Created by kelseykerr on 9/21/17.
 */

public class AppStatsFragment extends DialogFragment {
    private final String TAG = getClass().getSimpleName();
    private TextView startTime;
    private TextView mobileReceived;
    private TextView mobileSent;
    private TextView wifiReceived;
    private TextView wifiSent;
    private TextView wifiWakeups;
    private TextView mobileWakeups;
    AppStats appStats;

    public static AppStatsFragment newInstance(AppStats appStats) {
        AppStatsFragment fragment = new AppStatsFragment();
        fragment.appStats = appStats;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // request a window without the title
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.app_stats_detail, container, false);
        Log.i(TAG, "onCreate called");
        startTime = (TextView) view.findViewById(R.id.start_time);
        startTime.setText("Start Time: " + appStats.getStartTime());
        mobileReceived = (TextView) view.findViewById(R.id.mobile_packets_received);
        if (appStats.getMobileReceived() != null || appStats.getMobilePacketsReceived() != null) {
            mobileReceived.setText("Mobile Network Received: " + appStats.getMobileReceived() + "KB, " + appStats.getMobilePacketsReceived() + " packets");
        } else {
            mobileReceived.setText("Mobile Network Received: 0KB, 0 packets");
        }
        mobileSent = (TextView) view.findViewById(R.id.mobile_packets_sent);
        if (appStats.getMobileSent() != null || appStats.getMobilePacketsSent() != null) {
            mobileSent.setText("Mobile Network Sent: " + appStats.getMobileSent() + "KB, " + appStats.getMobilePacketsSent() + " packets");
        } else {
            mobileSent.setText("Mobile Network Sent: 0KB, 0 packets");
        }
        wifiReceived = (TextView) view.findViewById(R.id.wifi_packets_received);
        wifiReceived.setText("Wifi Network Received: " + appStats.getWifiReceived() + "KB, " + appStats.getWifiPacketsReceived() + " packets");
        wifiSent = (TextView) view.findViewById(R.id.wifi_packets_sent);
        wifiSent.setText("Wifi Network Sent: " + appStats.getWifiSent() + "KB, " + appStats.getWifiPacketsSent() + " packets");
        wifiWakeups = (TextView) view.findViewById(R.id.wifi_wakeups);
        wifiWakeups.setText("Wifi Wakeups: " + appStats.getWifiWakeups());
        mobileWakeups = (TextView) view.findViewById(R.id.mobile_wakeups);
        mobileWakeups.setText("Mobile Wakeups: " + appStats.getMobileWakeups());
        return view;
    }
}
