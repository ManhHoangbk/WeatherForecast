package com.ks.weatherforecast.share.model;

/**
 * Created by manhhoang on 5/6/18.
 */

public class CountryCode {

    private String id;
    private String countryCallingCodes;
    private String name;


    public static String createDBCountryCode = "CREATE TABLE CountryCode ("
            + " id TEXT PRIMARY KEY )";

    public CountryCode(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryCallingCodes() {
        return countryCallingCodes;
    }

    public void setCountryCallingCodes(String countryCallingCodes) {
        this.countryCallingCodes = countryCallingCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
