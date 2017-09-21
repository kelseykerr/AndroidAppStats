package com.kelseykerr.androidappstats;

import android.app.DialogFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kelseykerr.androidappstats.models.AppItem;
import com.kelseykerr.androidappstats.models.AppStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private HashMap<String, AppStats> appStatsMap;

    private ArrayList<AppItem> appItems;

    private ListView appList;

    private static AppItemAdapter appItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instantiateAppStatsMap();

        appList = (ListView) findViewById(R.id.app_list);
        appItemAdapter = new AppItemAdapter(appItems, getApplicationContext());
        appList.setAdapter(appItemAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppItem appItem = appItems.get(position);
                AppStats appStats = getBatteryStats(appItem.getPackageName());
                /*Snackbar.make(view, appItem.getAppName()+" was clicked", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/

                DialogFragment newFragment = AppStatsFragment.newInstance(appStats);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void instantiateAppStatsMap() {
        appStatsMap = new HashMap<>();
        appItems = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String packageName = line.replace("package:", "");
                Drawable appIcon = getPackageManager().getApplicationIcon(packageName);
                ApplicationInfo ai = getPackageManager().getApplicationInfo(packageName, 0);
                String appName = ai != null ? (String) getPackageManager().getApplicationLabel(ai) : null;
                appItems.add(new AppItem(appName, packageName, appIcon));
                appStatsMap.put(packageName, new AppStats());
                Log.d(TAG, "added package [" + packageName + "]");
            }
            Collections.sort(appItems);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't list packages, got error: " + e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package was not found, skipping");
        }
    }


    public AppStats getBatteryStats(String packageName) {
        try {
            AppStats appStats = new AppStats();
            Process process = Runtime.getRuntime().exec("dumpsys batterystats " + packageName);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line.contains(START_TIME_STRING)) {
                    String dateString = line.replace(START_TIME_STRING, "").trim();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH);
                    try {
                        Date startDate = format.parse(dateString);
                        appStats.setStartTime(startDate);
                        Log.i(TAG, "Set start time for " + packageName + " to: " + startDate);
                    } catch (ParseException e) {
                        Log.e(TAG, "Couldn't parse date string of [" + dateString + "].");
                    }
                } else if (line.contains(MOBILE_NETWORK_STRING)) {
                    setMobileStats(line, appStats);
                } else if (line.contains(WIFI_NETWORK_STRING)) {
                    setWifiStats(line, appStats);
                } else if (line.contains(MOBILE_WAKEUPS_STRING)) {
                    String wakeups = line.replace(MOBILE_WAKEUPS_STRING, "");
                    Integer mobileWakeups = Integer.parseInt(wakeups.trim());
                    if (appStats.getMobileWakeups() != null) {
                        appStats.setMobileWakeups(appStats.getMobileWakeups() + mobileWakeups);
                    } else {
                        appStats.setMobileWakeups(mobileWakeups);
                    }
                } else if (line.contains(WIFI_WAKEUPS_STRING)) {
                    String wakeups = line.replace(WIFI_WAKEUPS_STRING, "");
                    Integer wifiWakeups = Integer.parseInt(wakeups.trim());
                    if (appStats.getWifiWakeups() != null) {
                        appStats.setWifiWakeups(appStats.getWifiWakeups() + wifiWakeups);
                    } else {
                        appStats.setWifiWakeups(wifiWakeups);
                    }
                }
                line = bufferedReader.readLine();
            }
            return appStats;
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get battery stats, got error: " + e.getMessage());
            return null;
        }
    }

    private void setMobileStats(String line, AppStats appStats) {
        Log.i(TAG, "Got mobile network string for app: " + line);
        //Mobile network: 64.27KB received, 18.95KB sent (packets 132 received, 138 sent)
        String[] mobileNetworkData = line.split(",");
        String mobileReceived = mobileNetworkData[0];
        mobileReceived = mobileReceived.replace(MOBILE_NETWORK_STRING, "");
        mobileReceived = mobileReceived.replace("received", "");
        mobileReceived = mobileReceived.replace("KB", "").trim();
        Log.i(TAG, "MOBILE RECEIVED: " + mobileReceived);
        if (appStats.getMobileReceived() != null) {
            Double old = appStats.getMobileReceived();
            appStats.setMobileReceived(old + Double.parseDouble(mobileReceived.trim()));
        } else {
            appStats.setMobileReceived(Double.parseDouble(mobileReceived.trim()));
        }
        String string2 = mobileNetworkData[1];
        String[] sentAndReceived = string2.split("sent");
        String mobileSent = sentAndReceived[0].replace("sent", "");
        mobileSent = mobileSent.replace("KB", "").trim();
        Log.i(TAG, "MOBILE SENT: " + mobileReceived);
        if (appStats.getMobileSent() != null) {
            Double old = appStats.getMobileSent();
            appStats.setMobileSent(old + Integer.parseInt(mobileSent));
        } else {
            appStats.setMobileSent(Double.parseDouble(mobileSent));
        }
        appStats.setMobileSent(Double.parseDouble(mobileSent.trim()));
        String packetsReceived = sentAndReceived[1].replace("(packets", "");
        packetsReceived = packetsReceived.replace("received", "");
        if (appStats.getMobilePacketsReceived() != null) {
            appStats.setMobilePacketsReceived(appStats.getMobilePacketsReceived() + Integer.parseInt(packetsReceived));
        } else {
            appStats.setMobilePacketsReceived(Integer.parseInt(packetsReceived.trim()));
        }
        String sent = mobileNetworkData[2].replace("sent)", "").trim();
        if (appStats.getMobilePacketsSent() != null) {
            appStats.setMobilePacketsSent(appStats.getMobilePacketsSent() + Integer.parseInt(sent));
        } else {
            appStats.setMobilePacketsSent(Integer.parseInt(sent));
        }
    }

    private void setWifiStats(String line, AppStats appStats) {
        Log.i(TAG, "Got wifi network string for app: " + line);
        //Wi-Fi network: 64.27KB received, 18.95KB sent (packets 132 received, 138 sent)
        String[] wifiNetworkData = line.split(",");
        String wifiReceived = wifiNetworkData[0];
        wifiReceived = wifiReceived.replace(WIFI_NETWORK_STRING, "");
        wifiReceived = wifiReceived.replace("received", "");
        wifiReceived = wifiReceived.replace("KB", "");
        Log.i(TAG, "WIFI RECEIVED: " + wifiReceived);
        if (appStats.getWifiReceived() != null) {
            Double old = appStats.getWifiReceived();
            appStats.setWifiReceived(old + Double.parseDouble(wifiReceived.trim()));
        } else {
            appStats.setWifiReceived(Double.parseDouble(wifiReceived.trim()));
        }
        String string2 = wifiNetworkData[1];
        String[] sentAndReceived = string2.split("sent");
        String wifiSent = sentAndReceived[0].replace("sent", "");
        wifiSent = wifiSent.replace("KB", "");
        Log.i(TAG, "WIFI SENT: " + wifiReceived);
        if (appStats.getWifiSent() != null) {
            Double old = appStats.getWifiSent();
            appStats.setWifiSent(old + Double.parseDouble(wifiSent.trim()));
        } else {
            appStats.setWifiSent(Double.parseDouble(wifiSent.trim()));
        }
        String packetsReceived = sentAndReceived[1].replace("(packets", "");
        packetsReceived = packetsReceived.replace("received", "");
        if (appStats.getWifiPacketsReceived() != null) {
            appStats.setWifiPacketsReceived(appStats.getWifiPacketsReceived() + Integer.parseInt(packetsReceived));
        } else {
            appStats.setWifiPacketsReceived(Integer.parseInt(packetsReceived.trim()));
        }
        String sent = wifiNetworkData[2].replace("sent)", "").trim();
        if (appStats.getWifiPacketsSent() != null) {
            appStats.setWifiPacketsSent(appStats.getWifiPacketsSent() + Integer.parseInt(sent));
        } else {
            appStats.setWifiPacketsSent(Integer.parseInt(sent));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String START_TIME_STRING = "Current start time:";
    private static final String MOBILE_NETWORK_STRING = "Mobile network:";
    private static final String WIFI_NETWORK_STRING = "Wi-Fi network:";
    private static final String MOBILE_WAKEUPS_STRING = "Mobile radio AP wakeups:";
    private static final String WIFI_WAKEUPS_STRING = "WiFi AP wakeups:";
}
