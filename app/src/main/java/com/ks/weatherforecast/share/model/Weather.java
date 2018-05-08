package com.ks.weatherforecast.share.model;

/**
 * Created by manhhoang on 3/22/18.
 */

public class Weather {
    private Long id;
    private String main;
    private String description;
    private String icon;
    private float temp;
    private float pressure;
    private float humidity;
    private float tem_min;
    private float tem_max;
    private float sea_level;
    private float grnd_level;
    private long sunrise;
    private long sunset;

    public Weather() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getTem_min() {
        return tem_min;
    }

    public void setTem_min(float tem_min) {
        this.tem_min = tem_min;
    }

    public float getTem_max() {
        return tem_max;
    }

    public void setTem_max(float tem_max) {
        this.tem_max = tem_max;
    }

    public float getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(float grnd_level) {
        this.grnd_level = grnd_level;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }
}
