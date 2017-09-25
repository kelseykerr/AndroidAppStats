package com.kelseykerr.androidappstats;

import android.app.DialogFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
        instantiateAppStatsMap();

        appList = (ListView) findViewById(R.id.app_list);
        appItemAdapter = new AppItemAdapter(appItems, getApplicationContext());
        appList.setAdapter(appItemAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppItem appItem = appItems.get(position);
                AppStats appStats = getBatteryStats(appItem.getPackageName());
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
            Process process = Runtime.getRuntime().exec("dumpsys batterystats " + packageName + " -c");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            String uid = "";
            Integer partialWakeLockCount = 0;
            Integer fullWakeLockCount = 0;
            while (line != null) {
                String[] lineData = line.split(",");
                String id = lineData[3];
                switch (id) {
                    case "uid":
                        if (lineData[5].equals(packageName)) {
                            uid = lineData[4];
                            Log.d(TAG, "set UID of app to [" + uid + "]");
                        }
                        break;
                    case "wl":
                        if (lineData[1].equals(uid)) {
                            Log.d(TAG, "wake lock found");
                            //line: 9,10145,l,wl,*alarm*,0,f,0,0,0,0,148,p,12,0,42,248,248,bp,12,0,42,248,0,w,0,0,0,0
                            //values: 0, 0, 0, 0, wake lock, full time, 'f', full count, partial time, 'p', partial count, window time, 'w', window count
                            if (!lineData[5].equals("0")) {
                                fullWakeLockCount++;
                            } else {
                                partialWakeLockCount++;
                            }
                        }
                        break;
                    default:
                        break;
                }
                line = bufferedReader.readLine();
            }
            Log.d(TAG, "Full wake lock count is [" + fullWakeLockCount + "]");
            Log.d(TAG, "Partial wake lock count is [" + partialWakeLockCount + "]");
            appStats.setFullWakeLocks(fullWakeLockCount);
            appStats.setPartialWakeLocks(partialWakeLockCount);
            return appStats;
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get battery stats, got error: " + e.getMessage());
            return null;
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
