package com.ks.weatherforecast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.ks.weatherforecast.Activity.MainActivity;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.model.userinfo.Address;
import com.ks.weatherforecast.share.model.userinfo.Company;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhhoang on 4/25/18.
 */

public class ClientUtils {

    public static String getStrIcon(Context context, String iconId) {
        String icon;
        switch (iconId) {
            case "01d":
                icon = context.getString(R.string.icon_clear_sky_day);
                break;
            case "01n":
                icon = context.getString(R.string.icon_clear_sky_night);
                break;
            case "02d":
                icon = context.getString(R.string.icon_few_clouds_day);
                break;
            case "02n":
                icon = context.getString(R.string.icon_few_clouds_night);
                break;
            case "03d":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "03n":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "04d":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "04n":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "09d":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "09n":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "10d":
                icon = context.getString(R.string.icon_rain_day);
                break;
            case "10n":
                icon = context.getString(R.string.icon_rain_night);
                break;
            case "11d":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "11n":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "13d":
                icon = context.getString(R.string.icon_snow);
                break;
            case "13n":
                icon = context.getString(R.string.icon_snow);
                break;
            case "50d":
                icon = context.getString(R.string.icon_mist);
                break;
            case "50n":
                icon = context.getString(R.string.icon_mist);
                break;
            default:
                icon = context.getString(R.string.icon_weather_default);
        }

        return icon;
    }

    public static List<UserInfo> parseData(String data){
        List<UserInfo> userInfos = new ArrayList<>();
        try {

            JSONArray arr = new JSONArray(data);
            if(arr != null && arr.length() > 0){
                for (int i = 0 ; i < arr.length() ; i++){
                    JSONObject object = arr.getJSONObject(i);
                    UserInfo info = new UserInfo();
                    info.setId(getLong("id", object));
                    info.setEmail(getString("email", object));
                    info.setName(getString("name", object));
                    info.setUserName(getString("userName", object));
                    info.setWebsite(getString("website",object));
                    info.setPhone(getString("phone",object));

                    JSONObject companyObj = object.getJSONObject("company");
                    Company company = new Company();
                    company.setName(getString("name", companyObj));
                    company.setBs(getString("bs", companyObj));
                    company.setCatchPhrase(getString("catchPhrase",companyObj));
                    info.setCompany(company);

                    JSONObject objectAddr = new JSONObject();
                    Address address = new Address();
                    address.setCity(getString("city", objectAddr));
                    address.setStreet(getString("treet",objectAddr));
                    address.setSuite(getString("suite",objectAddr));
                    address.setZipcode(getString("zipcode",objectAddr));
                    info.setAddress(address);
                    userInfos.add(info);
//                    info.setName(getS);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInfos;
    }

    public static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    public static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    public static Double getDouble(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    public static long getLong(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getLong(tagName);
    }

    public static String localize(SharedPreferences sp, String preferenceKey, String defaultValueKey, Context context) {
        return localize(sp, context, preferenceKey, defaultValueKey);
    }

    public static String localize(SharedPreferences sp, Context context, String preferenceKey, String defaultValueKey) {
        String preferenceValue = sp.getString(preferenceKey, defaultValueKey);
        String result = preferenceValue;
        if ("speedUnit".equals(preferenceKey)) {
            if (MainActivity.speedUnits.containsKey(preferenceValue)) {
                result = context.getString(MainActivity.speedUnits.get(preferenceValue));
            }
        } else if ("pressureUnit".equals(preferenceKey)) {
            if (MainActivity.pressUnits.containsKey(preferenceValue)) {
                result = context.getString(MainActivity.pressUnits.get(preferenceValue));
            }
        }
        return result;
    }
}
