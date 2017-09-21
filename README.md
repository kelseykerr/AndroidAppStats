# AndroidAppStats
I wanna see all the data from dumpsys batterystats command in a human readable format

**To enable the app to aquire this data without rooting the device, plug in the device, install that app, then run the following commands:**

```
adb shell pm grant com.kelseykerr.androidappstats android.permission.BATTERY_STATS
```

```
adb shell pm grant com.kelseykerr.androidappstats android.permission.DUMP
```

```
adb shell pm grant com.kelseykerr.androidappstats android.permission.PACKAGE_USAGE_STATS
```
