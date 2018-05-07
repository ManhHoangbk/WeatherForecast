package com.ks.weatherforecast.share.model;

/**
 * Created by manhhoang on 5/6/18.
 */

public class City {
    private Long id;
    private String name;
    private double lat;
    private double lon;
    private String countryCode;

    public static String createDB = "CREATE TABLE City ("
            + " id INTEGER PRIMARY KEY,"
            + " name TEXT,"
            + " lat INTEGER,"
            + " lon INTEGER,"
            + " countryCode TEXT )";


    public City() {
    }

    public City(Long id, String name, double lat, double lon, String countryCode) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

