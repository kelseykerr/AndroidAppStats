package com.kelseykerr.androidappstats.models;

/**
 * Created by kelseykerr on 9/21/17.
 */

public class AppStats {

    private Integer partialWakeLocks;
    private Integer fullWakeLocks;



    public AppStats() {

    }


    public Integer getPartialWakeLocks() {
        return partialWakeLocks;
    }

    public void setPartialWakeLocks(Integer partialWakeLocks) {
        this.partialWakeLocks = partialWakeLocks;
    }

    public Integer getFullWakeLocks() {
        return fullWakeLocks;
    }

    public void setFullWakeLocks(Integer fullWakeLocks) {
        this.fullWakeLocks = fullWakeLocks;
    }
}
