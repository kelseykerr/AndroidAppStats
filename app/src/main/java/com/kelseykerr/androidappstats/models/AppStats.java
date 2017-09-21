package com.kelseykerr.androidappstats.models;

import java.util.Date;

/**
 * Created by kelseykerr on 9/21/17.
 */

public class AppStats {

    //from text "Current start time: 2017-09-21-01-34-49"
    private Date startTime;

    //from Memory Stats: "Mobile network: 22.21KB received, 8.76KB sent (packets 57 received, 73 sent)"
    private Integer mobilePacketsReceived;
    private Integer mobilePacketsSent;
    private Double mobileReceived;
    private Double mobileSent;

    //from "Mobile radio active: 54s 541ms (6.0%) 10x @ 420 mspp"
    //54s 541ms (6.0%) 10x @ 420 mspp
    private String mobileRadioActive;

    //from "Wi-Fi network: 59.20KB received, 20.24KB sent (packets 114 received, 126 sent)"
    private Integer wifiPacketsReceived;
    private Integer wifiPacketsSent;
    private Double wifiReceived;
    private Double wifiSent;



    public AppStats() {

    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getMobilePacketsReceived() {
        return mobilePacketsReceived;
    }

    public void setMobilePacketsReceived(Integer mobilePacketsReceived) {
        this.mobilePacketsReceived = mobilePacketsReceived;
    }

    public Integer getMobilePacketsSent() {
        return mobilePacketsSent;
    }

    public void setMobilePacketsSent(Integer mobilePacketsSent) {
        this.mobilePacketsSent = mobilePacketsSent;
    }

    public Double getMobileReceived() {
        return mobileReceived;
    }

    public void setMobileReceived(Double mobileReceived) {
        this.mobileReceived = mobileReceived;
    }

    public Double getMobileSent() {
        return mobileSent;
    }

    public void setMobileSent(Double mobileSent) {
        this.mobileSent = mobileSent;
    }

    public String getMobileRadioActive() {
        return mobileRadioActive;
    }

    public void setMobileRadioActive(String mobileRadioActive) {
        this.mobileRadioActive = mobileRadioActive;
    }

    public Integer getWifiPacketsReceived() {
        return wifiPacketsReceived;
    }

    public void setWifiPacketsReceived(Integer wifiPacketsReceived) {
        this.wifiPacketsReceived = wifiPacketsReceived;
    }

    public Integer getWifiPacketsSent() {
        return wifiPacketsSent;
    }

    public void setWifiPacketsSent(Integer wifiPacketsSent) {
        this.wifiPacketsSent = wifiPacketsSent;
    }

    public Double getWifiReceived() {
        return wifiReceived;
    }

    public void setWifiReceived(Double wifiReceived) {
        this.wifiReceived = wifiReceived;
    }

    public Double getWifiSent() {
        return wifiSent;
    }

    public void setWifiSent(Double wifiSent) {
        this.wifiSent = wifiSent;
    }
}
