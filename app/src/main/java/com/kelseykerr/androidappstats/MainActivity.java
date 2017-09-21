package com.kelseykerr.androidappstats;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kelseykerr.androidappstats.models.AppItem;
import com.kelseykerr.androidappstats.models.AppStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
                Snackbar.make(view, appItem.getAppName()+" was clicked", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
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



    public void getBatteryStats() {
        try {
            Process process = Runtime.getRuntime().exec("dumpsys batterystats");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder allData = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                allData.append(line).append('\n');
                Log.i(TAG, "***battery data: " + line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get battery stats, got error: " + e.getMessage());
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
}
