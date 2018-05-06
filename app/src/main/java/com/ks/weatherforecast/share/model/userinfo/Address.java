package com.ks.weatherforecast.share.model.userinfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manhhoang on 4/25/18.
 */

public class Address {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Map<String, String> maps = new HashMap<>();


    public Address() {
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Map<String, String> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, String> maps) {
        this.maps = maps;
    }
}
