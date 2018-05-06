package com.ks.weatherforecast.share.model;

/**
 * Created by manhhoang on 3/22/18.
 */

public class Rain {
    private String time;
    private float ammount;

    public Rain() {
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public float getAmmount() {
        return ammount;
    }
    public void setAmmount(float ammount) {
        this.ammount = ammount;
    }
}
