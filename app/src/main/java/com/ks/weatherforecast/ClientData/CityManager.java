package com.ks.weatherforecast.ClientData;

import android.util.Log;

import com.ks.weatherforecast.share.model.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by manhhoang on 5/7/18.
 */

public class CityManager {
    static Map<String, List<City>> mapsCityOfCountry = new HashMap<>();

    public static void initCity(InputStream in){
        List<City> citis = new ArrayList<>();
        Set<String> setsCountry = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new InputStreamReader(in));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if(!mLine.isEmpty()){
                    String[] arr = mLine.split("\t");
                    City city = new City(Long.parseLong(arr[0]), arr[1], Double.parseDouble(arr[2]) , Double.parseDouble( arr[3]),  arr[4]);
                    if(!mapsCityOfCountry.containsKey(city.getCountryCode())){
                        mapsCityOfCountry.put(city.getCountryCode(), new ArrayList<City>());
                    }
                    mapsCityOfCountry.get(city.getCountryCode()).add(city);
                    setsCountry.add(city.getCountryCode());
                }
            }
        } catch (IOException e) {
            Log.e("ex: ", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}
