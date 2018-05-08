package com.ks.weatherforecast.ClientData;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Filter;

import com.ks.weatherforecast.Activity.MainActivity;
import com.ks.weatherforecast.Activity.SearchActivity;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.model.City;
import com.ks.weatherforecast.share.model.Location;
import com.ks.weatherforecast.share.model.WeatherForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

    public static List<WeatherForecast> getCity(Context context, SearchActivity activity, String query) {
        List<WeatherForecast> citySearchList = new ArrayList<>();
        City city;
        HttpURLConnection connection = null;

        try {
            URL url = provideURL(activity, context, query);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                byteArray.write(buffer, 0, bytesRead);
            }
            byteArray.close();

            JSONObject jsonObject = new JSONObject(byteArray.toString());
            Log.v("jsonObject: ", jsonObject.toString());
            JSONArray listArray = jsonObject.getJSONArray("list");

            int listArrayCount = listArray.length();

            for (int i = 0; i != listArrayCount; ++i) {
                WeatherForecast forecast = new WeatherForecast();
                JSONObject resultObject = listArray.getJSONObject(i);
                forecast.setName(resultObject.getString("name"));

                JSONObject coordObject = resultObject.getJSONObject("coord");
                Location location = new Location();

                JSONObject sysObject = resultObject.getJSONObject("sys");
                forecast.setCountry(sysObject.getString("country"));
                citySearchList.add(forecast);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return citySearchList;
    }


    private static URL provideURL(Activity activity, Context context, String query) throws UnsupportedEncodingException, MalformedURLException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String apiKey = sp.getString("apiKey", activity.getResources().getString(R.string.apiKey));
        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/");
        urlBuilder.append("find").append("?");
        String city  = sp.getString("city", query);
        urlBuilder.append("q=").append(URLEncoder.encode(city, "UTF-8"));
        urlBuilder.append("&type=").append("like");
        urlBuilder.append("&lang=").append(getLanguage());
        urlBuilder.append("&cnt=15");
        urlBuilder.append("&appid=").append(apiKey);
        return new URL(urlBuilder.toString());
    }

    private static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("cs")) {
            language = "cz";
        }
        return language;
    }

}
