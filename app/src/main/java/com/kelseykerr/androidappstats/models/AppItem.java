package com.kelseykerr.androidappstats.models;

import android.graphics.drawable.Drawable;

/**
 * Created by kelseykerr on 9/21/17.
 */

public class AppItem implements Comparable<AppItem> {

    private Drawable appIcon;

    private String appName;

    private String packageName;

    public AppItem() {

    }

    public AppItem(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int compareTo(AppItem appItem) {
        return this.appName.compareTo(appItem.appName);
    }
}
